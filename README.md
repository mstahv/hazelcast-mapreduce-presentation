# Hazelcast Map Reduce Tutorial Code

This is application contains MapReduce programming examples used for "Practical introduction to MapReduce" [webinar](https://vaadin.com/forum#!/thread/9203573). Examples are modified modified from the previous Hazelcast MR tutorial webinar available also available in [Youtube](https://www.youtube.com/watch?v=YCZGKoBoXsg)

Requirements to try out the examples yourself:
 
 * Java 8
 * Maven (or an IDE with built in Maven)

To start, issue maven command:

    mvn wildfly:run

This will start a WildFly server that will serve the UI. You can also directly deploy to application to your favorite Java EE server. When you enter the the application UI ( at http://localhost:8080/ ), the demo cluster with two cluster nodes is started by HazelcastService (CDI application scoped bean), which will take couple of seconds while the nodes discover each other.

Read the examples codes, modify and play with them, that is the best way to get started!
