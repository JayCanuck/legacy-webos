NAME = mplayer
VERSION = 32749
DEPENDS = common/tisgxgfx common/adrenogfx common/libpdl

SRC_SVN_REV = svn://svn.mplayerhq.hu/mplayer/trunk

include ../../../support/download.mk

.PHONY: unpack
unpack: build/.unpacked

build/.unpacked: ${DL_DIR}/${NAME}-svn-r${VERSION}.tar.gz
	rm -rf build
	mkdir -p build/src
	tar -C build/src -xf ${DL_DIR}/${NAME}-svn-r${VERSION}.tar.gz
	cat ${NAME}-svn-r${VERSION}-webos.patch | patch -d build/src -p1
	touch $@

include ../../../support/staging.mk

stage-local:: build/${ARCH}.built

include ../../../support/cross-compile.mk

build: stage

# Prevent the ARCH= variable being passed to recursive make invocations
MAKEOVERRIDES = 

build/%.built: build/.unpacked
	rm -rf $@ build/$*
	cp -R build/src build/$*
	( cd build/$* ; ${SB2} sh make.sh )
	( cd build/$* ; ${SB2} make )
	touch $@

clobber::
	rm -rf build

