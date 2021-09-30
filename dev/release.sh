git co master
mlsql_plugin_tool build  scala211
mvn clean deploy -DskipTests -Pdisable-java8-doclint -Prelease-sign-artifacts

mlsql_plugin_tool build  scala212
mvn clean deploy -DskipTests -Pdisable-java8-doclint -Prelease-sign-artifacts -Pscala-2.12
git co .
