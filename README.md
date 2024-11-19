# QuizApp
Designed with the upkeep of elderly mental fortitude in mind, this QuizApp allows for easy facilitation of quiz games in an elderly care home. Individual games can be played by each user, while admins are able to host joinable sessions for larger groups on websockets. Autoplay mode, separate UI for user and admin, and tallied answer counts allows to a seamless experience when playing or hosting a game. Admins are able to create quizzes with various forms of media, including audio, images, and video. Add variety to quizzes by having images for answers, and when a question is answered in a video or audio clip, that clip plays automatically when the correct answer is selected. Improve the lives of the elderly and their caretakers with this robust, RESTful, fullstack application. 

## 1. Project Description
### Key Features of QuizApp:
- 1. User Interfaces
    - Standard Interface:
        - Join an admin moderated session, in sync with existing players, from the main page based on your chosen administrator
        - Select a quiz from a variety of categories, and play through the quiz
        - Set a quiz to autoplay mode, or make it dependant on answer selection
    - Admin Interface (Includes all features found in the standard interface):
        - Create a new category, or add a quiz to an existing one, through the quiz creation screen
        - Add image media to quizzes or categories
        - Delete quizzes, questions, and answers
        - Create a new quiz, with the ability to add video, audio, or images to each question
        - Add image answers, or attach a video or audio clip to the correct answer of a question
        - Edit quizzes and their questions
        - Host a moderated session of a specific quiz
        - View the answer count for the current question in moderated mode
        - End the moderated session
- 2. Database Management
    - When a user signs up, an individual user is created in the mySQL database
    - Different user types have different permissions
- 3. File Management
    - Media is hosted locally on GitHub
- 5. Security
    - Authorization required to enter the application

## 2. Team:
Roles:
- Catherine Queenan
    - Project Manager, Back-end Developer
- Eugenie Kim
    - Tech Lead, Back-end Developer
- Soomin Jeong
    - Full-Stack Developer, UI/UX Designer

## 3. Technologies and Resources Used
- Languages and Libraries
    - Javascript
    - Java 17
    - HTML
    - CSS
    - MySQL
- Packages
    - Catalina
    - Jakarta
    - Jasper
    - JSON
    - MySQL Connector
    - Servlet API
    - TomCat API
    - Websocket API
    - AspectJ
- Database
    - MySQL
- External Tools
    - Visual Studio Code
    - SourceTree
    - GitHub
    - Tomcat
    - Docker
- Styling
    - GoogleAPIs fonts

## 4. Complete Setup/Installation
- 1. Clone the repository
    - `git clone https://github.com/Catherine-Queenan/CS-QuizGame`
- 2. Navigate to the project directory
    - `cd CS-QuizGame`
- 3. Install dependencies
    - Included in run of GitHub Actions
- 4. Set up Docker
    - Navigate to this link and follow install instructions
        - https://docs.docker.com/get-started/get-docker/ 
    - Make sure Docker is running on your device
    - Enter in terminal `docker-compose up`
- 5. Enter Server
    - Laod the page at localhost:8080/app/login
- 6. Admin creation
    - Enter Docker desktop and select the quizGame container
    - In SQL Docker select the exec tab 
    - Enter: `mysql -uroot -p`
    - Enter password: root
    - Enter command `update users set role = 'a' where username = 'YOUR_USERNAME';`

## 6. Known Bugs and Limitations
- Media answers are non-functional in moderated mode
- Audio question media is non-functional in moderated mode
- Logout periodically sends the user to a page of raw JSON

## 7. Features for the Future
- Encryption of data
- Ability to edit individual  answers
- Tags to apply to various quizzes to give more information to users

## 8. Database Cluster Structure:
```
├── users
│   ├── id: BINARY(16)
│   ├── username
│   ├── password
│   └── role
├── categories
│   └── name:
├── quizzes
│   ├── name
│   ├── description
│   └── category_name
├── tags
│   └── name
├── quiz_tags
│   ├── quiz_name
│   └── tag_name
├── questions
│   ├── id
│   ├── quiz_name
│   ├── question_text
│   └── question_type
├── answers
│   ├── id
│   ├── question_id
│   ├── answer_text
│   ├── is_correct
│   └── answer_type
├── media
│   ├── id
│   ├── description
│   ├── media_type
│   ├── media_file_path
│   ├── media_filename
│   ├── media_start
│   └── media_end
├── question_media
│   ├── question_id
│   ├── media_id
├── quiz_media
│   ├── quiz_name
│   └── media_id
├── answer_media
│   ├── answer_id
│   └── media_id
├── category_media
│   ├── category_name
│   └── media_id
├── moderated_sessions
│   ├── session_id
│   ├── moderator_id
│   ├── start_time
│   ├── end_time
│   ├── session_status
│   ├── created_at
│   ├── updated_at
│   └── quiz_name
```

## 9. Contents of Folder
```
├── .gitignore                              # Specifies files to be ignored by Git
├── docker-compose.yml                      # Compose file for Docker
├── Dockerfile                              #
├── init.sql                                # Initializes database through Docker
├── README.md                               # This file
├── pom.xml                                 # JDK 17 setup
├── Tasks_BreakdownREADME.md                # Breakdown of tasks completed for assignment submission
├── wait-for-db.sh                          # MySQL waiter
├── .github/                                # Contains gitHub actions information
│   ├── workflows/                          # Sub folder
│       ├── DockerPushTest.yaml             # Automatically tests the Docker
│       ├── TomCatBuild.yml                 # Compiles the app and installs dependencies
├── scripts/                                # Contains universal script
│   ├── logout.js                           # Script loaded into all logged in pages to handle logout                               
├── target/                                 # Holds app.war file
│   ├── classes/                            #
│       ├── app.war                         #
│   ├── test-classes/                       #
│       ├── app.war                         #
├── views/                                  # Holds all html and jsp files for client interface
│   ├── addQuestion.jsp                     # Page for adding a new question to a quiz
│   ├── answerMediaUpload.jsp               # Upload page for answer media
│   ├── createQuiz.jsp                      # Quiz creation page
│   ├── edit.jsp                            # Edit quizzes
│   ├── editQuestions.jsp                   # Edit individual questions
│   ├── error.jsp                           # Redirect page for errors
│   ├── main.jsp                            # Landing page of application
│   ├── login.html                          # Landing page of application if not logged in
│   ├── moderateMode.jsp                    # Moderated servlet page
│   ├── questions.jsp                       # Individual questions in a quiz
│   ├── quiz.jsp                            # Quizzes within categories
│   ├── quizEnd.html                        # Page loaded when a quiz ends
│   ├── signup.html                         # Page for new users to sign up from
├── WEB-INF/                                # Contains all java files and dependencies
│   ├── classes/                            # Java files for server code
│       ├── aspects/                        # Aspects
│           ├── LoggingAspect.java          # Aspects for logging
│       ├── META-INF                        # Meta information
│           ├── aop.xml                     # Inclusion of logging
│       ├── AClass.java                     # Class for database table
│       ├── AClassFactory.java              # Factory for AClass
│       ├── AddQuestionServlet.java         # Servlet for adding a question
│       ├── Answer.java                     # Class for answer objects
│       ├── Category.java                   # Class for category objects
│       ├── CategoryServlet.java            # Servlet for categories
│       ├── CreateQuizServlet.java          # Servlet for quiz creation
│       ├── DatabaseUtil.java               # Database management and loading
│       ├── DeleteQuestionServlet.java      # Servlet for deletion of questions
│       ├── DeleteQuizServlet.java          # Servlet for deletion of quizzes
│       ├── EditQuestionsServlet.java       # Servlet for editing questions
│       ├── EditServlet.java                # Servlet for editing quizzes
│       ├── ErrorServlet.java               # Servlet for error page
│       ├── IRepository.java                # DAO
│       ├── LoginServlet.java               # Servlet for login page
│       ├── LogoutServlet.java              # Servlet for logging out
│       ├── MainServlet.java                # Servlet for home page of app
│       ├── Media.java                      # Class for media objects
│       ├── ModerateModeServlet.java        # Servlet for moderation mode
│       ├── Question.java                   # Class for question objects
│       ├── QuestionServlet.java            # Servlet for questions
│       ├── QuestionWebSocket.java          # Websocket for moderated quiz loading
│       ├── Quiz.java                       # Class for quiz objects
│       ├── QuizEndServlet.java             # Servlet for quiz end screen
│       ├── QuizServlet.java                # Servlet for quizzes
│       ├── Repository.java                 # Repository of database statements as objects
│       ├── SessionStatusServlet.java       # Servlet for moderated session
│       ├── SignupServlet.java              # Servlet for signing up as a user
│       ├── UpdateAutoplay.java             # Servlet to manage autoplay
│   ├── lib/                                # Dependencies
│       ├── annotations-api.jar             #
│       ├── catalina-ant.jar                #
│       ├── catalina-ha.jar                 #
│       ├── catalina-ssi.jar                #
│       ├── catalina-storeconfig.jar        #
│       ├── catalina-tribes.jar             #
│       ├── catalina.jar                    #
│       ├── ecj-4.27.jar                    #
│       ├── el-api.jar                      #
│       ├── jakarta.json-api-2.0.0.jar      #
│       ├── jakarta.websocket-api-2.0.0.jar #
│       ├── jakartaee-migration-1.0.8-shaded.jar    #
│       ├── jasper-el.jar                   #
│       ├── jasper.jar                      #
│       ├── jaspic-api.jar                  #
│       ├── json-20240303.jar               #
│       ├── jsp-api.jar                     #
│       ├── mysql-connector-j-9.0.0.jar     #
│       ├── servlet-api.jar                 #
│       ├── tomcat-api.jar                  #
│       ├── tomcat-coyote-ffm.jar           #
│       ├── tomcat-dbcp.jar                 #
│       ├── tomcat-i18n-cs.jar              #
│       ├── tomcat-i18n-de.jar              #
│       ├── tomcat-i18n-es.jar              #
│       ├── tomcat-i18n-fr.jar              #
│       ├── tomcat-i18n-ja.jar              #
│       ├── tomcat-i18n-ko.jar              #
│       ├── tomcat-i18n-pt-BR.jar           #
│       ├── tomcat-i18n-ru.jar              #
│       ├── tomcat-i18n-zh-CN.jar           #
│       ├── tomcat-jdbc.jar                 #
│       ├── tomcat-jni.jar                  #
│       ├── tomcat-util-scan.jar            #
│       ├── tomcat-util.jar                 #
│       ├── tomcat-websocket.jar            #
│       ├── websocket-api.jar               #
│       ├── websocket-client-api.jar        #
│   ├── web.xml                             # Server mapping
├── public/                                 # Contains public assets such as css and files
│   ├── css/                                # Styling sheets
│       ├── reset.css                       # global styling sheet
│   ├── media/                              # holds media loaded into the application by users
```