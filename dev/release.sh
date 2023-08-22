#git co master
#mlsql_plugin_tool  scala211
#mvn clean deploy -DskipTests -Pdisable-java8-doclint -Prelease-sign-artifacts

#mlsql_plugin_tool  scala212
mvn clean deploy -DskipTests -Pdisable-java8-doclint -Prelease-sign-artifacts
#git co .
