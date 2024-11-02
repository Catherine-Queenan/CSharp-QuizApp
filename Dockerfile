# Use the official Tomcat image as the base image
FROM tomcat:6.0

# Copy the WAR file to the Tomcat webapps directory
COPY target/app.war /usr/local/tomcat/webapps/
COPY ./WEB-INF/lib/*.jar /usr/local/tomcat/lib/


# Expose Tomcat’s default port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
