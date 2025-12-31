group = "${rootProject.group}.protocol"

dependencies {
    compileOnly("org.apache.logging.log4j:log4j-api:2.22.1")
    compileOnly("com.google.code.gson:gson:2.13.2")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
}
