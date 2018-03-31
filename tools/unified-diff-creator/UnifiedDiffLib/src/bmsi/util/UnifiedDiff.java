package bmsi.util;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.swing.JOptionPane;

/**
 *
 * @author Jason Robitaille
 */
public class UnifiedDiff {
    private File original;
    private File revised;
    private String meta;

    public UnifiedDiff(File f1, File f2) {
        original = f1;
        revised = f2;
        meta = null;
    }

    public void addMeta(String name, String version, String author, String description) {
        meta = "";
        if(name!=null)
            meta += "Name: " + name + "\n";
        if(version!=null)
            meta += "Version: " + version + "\n";
        if(author!=null)
            meta += "Author: " + author + "\n";
        if(description!=null)
            meta += "Description: " + description + "\n";
    }

    public File createDiff(String out, String dest) {
        return createDiff(new File(out), dest);
    }

    public File createDiff(File out, String dest) {
        File result = out;
        try {
            String[] a = DiffPrint.slurp(original);
            String[] b = DiffPrint.slurp(revised);
            Diff d = new Diff(a,b);
            Diff.change script = d.diff_2(false);
            if (script == null) {
                JOptionPane.showMessageDialog(null, "Error: No differences");
                result = null;
            } else {
                DiffPrint.Base p = new DiffPrint.UnifiedPrint(a,b);
                FileWriter fw = new FileWriter(result);
                p.setOutput(fw);
                p.print_header(".orig" + dest, dest);
                p.print_script(script);
                p.close();
                doubleCheckChunkHeaders(result);
            }
        } catch(Exception e) {
            result = null;
        }
        return result;
    }

    private void doubleCheckChunkHeaders(File out) {
        try {
            String[] patch = DiffPrint.slurp(out);
            int currHeader = -1;
            int before = 0;
            int after = 0;
            int offset = 0;
            ChunkHeader curr = null;
            for(int i=0; i<patch.length; i++) {
                if(patch[i].startsWith("@@")) {
                    if(currHeader!=-1 && curr!=null) { //finish
                        curr.setBeforeRange(before);
                        curr.setAfterRange(after);
                        patch[currHeader] = curr.toString();
                    }
                    currHeader = i;
                    curr = new ChunkHeader(patch[i]);
                    offset += (after - before);
                    curr.setOffset(offset);
                    before = 0;
                    after = 0;
                } else if(patch[i].startsWith("+")) {
                    after++;
                } else if(patch[i].startsWith("-")) {
                    before++;
                } else if(patch[i].startsWith(" ")) {
                    before++;
                    after++;
                }
            }
            if(currHeader!=-1 && curr!=null) { //last header
                curr.setBeforeRange(before);
                curr.setAfterRange(after);
                patch[currHeader] = curr.toString();
            }
            writeToFile(out, patch);
        } catch(Exception e) {}
    }

    private boolean addNoNewLineFlag() {
        boolean result = false;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(original));
            char curr = '\n';
            int num;
            while((num=br.read())!=-1) {
                curr = (char) num;
            }
            result = (curr != '\n');
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void writeToFile(File out, String[] patch) throws Exception {
        if(out.exists()) {
            out.delete();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        if(meta!=null) {
            if(meta.length()>0) {
                bw.write(meta + "\n");
            }
        }
        for(int i=0; i<patch.length; i++) {
            if(patch[i].length()>0) { //ignore empty strings
                bw.write(patch[i] + "\n");
            }
        }
        if(addNoNewLineFlag()) {
            bw.write("\\ No newline at end of file\n");
        }
        bw.flush();
        bw.close();
    }

    private class ChunkHeader {
        private String[] before;
        private String[] after;

        public ChunkHeader(String header) {
            String[] tokens = header.split(" ");
            before = tokens[1].substring(1).split(",");
            after = tokens[1].substring(1).split(",");
        }

        public void setBeforeRange(int range) {
            before[1] = String.valueOf(range);
        }

        public void setAfterRange(int range) {
            after[1] = String.valueOf(range);
        }

        public void setOffset(int offset) {
            int i = Integer.parseInt(before[0]);
            after[0] = String.valueOf(i + offset);
        }

        @Override
        public String toString() {
            return "@@ -" + before[0] + "," + before[1] + " +" + after[0] + "," + after[1] + " @@";
        }
    }
}
