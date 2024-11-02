# Use the official Tomcat image as the base image
FROM tomcat:latest

# Copy the WAR file to the Tomcat webapps directory
COPY target/app.war /usr/local/tomcat/webapps/
COPY ./WEB-INF/lib/mysql-connector-j-9.0.0.jar /usr/local/tomcat/lib/

# Expose Tomcat’s default port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
