/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.canucksoftware.themebuilder;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.twicom.qdparser.*;

/**
 *
 * @author Jason
 */
public class XMLHandler {
    private TaggedElement root;
    private XMLReader document;
    private File xmlFile;

    public XMLHandler(String path) {
        this(new File(path));
    }

    public XMLHandler(File xml) {
        xmlFile = xml;
        try {
            FileReader fstream = new FileReader(xmlFile);
            document = new XMLReader("", fstream);
            root = document.parse();
            fstream.close();
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERROR: Unable to read xml file.");
        }
    }

    public TaggedElement getRoot() {
        return root;
    }

    public String getContent(Element ele) {
        String s;
        try {
            s = ele.toString();
            s = s.substring(s.indexOf(">")+1, s.lastIndexOf("<"));
        } catch(Exception e) {
            s = "";
        }
        return s;
    }

    public TaggedElement setContent(TaggedElement parent, TaggedElement ele, String content) {
        int i = parent.findIndex(ele.getName());
        Element newEle = Element.newElement("<" + ele.getTag() + ">" + content + "</" + ele.getTag() + ">");
        parent.remove(ele);
        parent.add(i, newEle);
        return (TaggedElement) newEle;
    }

    public void updateFile() {
        try {
            FileWriter fstream = new FileWriter(xmlFile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.newLine();
            out.write(root.toString());
            out.flush();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERROR: Unable to write to xml file.");
        }
    }

    public File getFile() {
        return xmlFile;
    }

    public static XMLHandler createNewXML(String file) {
        return createNewXML(file, "data");
    }

    public static XMLHandler createNewXML(File f) {
        return createNewXML(f, "data");
    }

    public static XMLHandler createNewXML(String file, String rootTag) {
        return createNewXML(new File(file), rootTag);
    }

    public static XMLHandler createNewXML(File f, String rootTag) {
        XMLHandler result = null;
        try {
            if(f.exists())
                f.delete();
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.newLine();
            out.write("<" + rootTag + ">");
            out.newLine();
            out.write("</" + rootTag + ">");
            out.newLine();
            out.flush();
            out.close();
            result = new XMLHandler(f);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR: Unable to write to xml file.");
        }
        return result;
    }
}
