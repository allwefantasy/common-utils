mvn clean deploy -DskipTests -Pdisable-java8-doclint -Prelease-sign-artifacts

./dev/change-scala-version 2.12
mvn clean deploy -DskipTests -Pdisable-java8-doclint -Prelease-sign-artifacts -Pscala-2.12
git co .
