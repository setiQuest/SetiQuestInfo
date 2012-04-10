all:
	javac -classpath $(CLASSPATH) -d ${LIB_DIR}  *.java

doc:
	mkdir -p ${PROJ_HOME}/doc/renderer
	javadoc -footer "<p>Copyright 2012 the SETI Institute. Visit <a href=\"http://setiquest.org\" target=\"_other\">setiQuest.org</a>" -header "<h1>The Data Renderer for the SETILive! Project</h1>" -doctitle "<h1>The Data Renderer for the SETILive! Project</h1>" -linksource -d ${PROJ_HOME}/doc/renderer *.java

clean:
	-rm -rf ${PROJ_HOME}/doc/renderer
	-rm -rf ${LIB_DIR}/setiquest/renderer/*.class

