
all:
	#echo ${CLASSPATH}
	javac -classpath $(CLASSPATH) -d ${LIB_DIR}  *.java

doc:
	mkdir -p ${PROJ_HOME}/doc/common
	javadoc -footer "<p>Copyright 2012 the SETI Institute. Visit <a href=\"http://setiquest.org\" target=\"_other\">setiQuest.org</a>" -header "<h1>Common classes used for the SetiQuestInfo projects</h1>" -doctitle "<h1>Common classes used for the SetiQuestInfo projects</h1>" -linksource -d ${PROJ_HOME}/doc/common *.java



clean:
	-rm -rf ${PROJ_HOME}/doc/common
	-rm -rf ${LIB_DIR}/setiquest/common/*.class

