
function BuildPage() {

    const currentPath = window.location.pathname;
    const pathSegments = currentPath.split('/');
    console.log(pathSegments);

    // Extract the category name from URL parameters
    const categoryName = pathSegments[2];

    pathSegments.pop();

    // Construct the new path dynamically
    const newPath = pathSegments.join('/') + `/api/play`;
    const homePath = pathSegments.join('/') + `/home`;

    // console.log(newPath)
    fetch(newPath, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    }).then(response => {
        if (!response.ok) {
            console.error('Response status:', response.status);
            throw new Error('Failed to fetch question');
        }
        return response.json(); // Change this temporarily to text() instead of json()
    }).then(data => {
        console.log(data);
        const questionContainer = document.getElementById("questionDiv");
        questionContainer.innerHTML = '';

        const currQuestionSpan = document.getElementById("currQuestion");
        currQuestionSpan.innerHTML = data.currQuestion;

        const quizSizeSpan = document.getElementById("quizSize");
        quizSizeSpan.innerHTML = data.quizSize;

        const autoPlayInput = document.getElementById("autoplay");
        autoPlayInput.value = data.autoPlayEnabled;

        // Render question
        if (data.quizSize <= 0) {
            questionContainer.innerHTML = `
                <p class="errorMsg">The quiz " ${data.quizName} " is empty!</p>
                <form class="errorBtnWrap" action="home"><button class="homeBtn errorHome" type="Submit">Return Home</button></form>
            `;

        } else {
            const questionDiv = document.createElement("div");
            questionDiv.classList.add("question");

            const questionText = document.createElement("p");
            questionText.classList.add("questionText");
            questionText.innerHTML = data.question.question_text;
            questionDiv.appendChild(questionText);

            if (data.question.question_type != "TEXT") {
                const questionMedia = insertQuestionMedia(data.question);
                questionDiv.appendChild(questionMedia);
            }

            insertAnswers(data.question.answers, questionDiv);
            questionContainer.appendChild(questionDiv);
        }
    });
}

function insertQuestionMedia(question) {
    const mediaDiv = document.createElement("div");
    mediaDiv.id = "questionMedia";

    const imgDiv = document.createElement("div");
    imgDiv.classList.add("imgWrap");

    switch (question.media_type) {
        case "VID":
            insertVideo(question, "question", mediaDiv);
            break;
        case "IMG":
            insertImage(question, imgDiv);
            return imgDiv;
        case "AUD":
            insertAudio(question, "question", mediaDiv);
            break;
    }

    return mediaDiv;

}

function insertAnswerMedia(answer) {
    const mediaDiv = document.createElement("div");
    mediaDiv.id = "mediaAnswer";
    mediaDiv.style.display = "none";

    switch (question.media_type) {
        case "VID":
            insertVideo(answer, "answer", mediaDiv);
            break;
        case "AUD":
            insertAudio(answer, "answer", mediaDiv);
            break;
    }
    return mediaDiv;
}

function insertVideo(entry, table, div) {
    const link = entry.media.media_file_path.split("=")[1];
    const videoId = createInputElement("hidden", link, `videoId-${table}`);
    const videoStart = createInputElement("hidden", entry.media.media_start, `videoStart-${table}`);
    const videoEnd = createInputElement("hidden", entry.media.media_end, `videoEnd-${table}`);
    const playerDiv = document.createElement("div");
    playerDiv.id = `player-${table}`;

    div.appendChild(videoId);
    div.appendChild(videoStart);
    div.appendChild(videoEnd);
    div.appendChild(playerDiv);
}

function insertImage(entry, div) {
    div.style.display = "none";

    const img = document.createElement("img");

    img.alt = entry.media.description;
    img.width = 300;
    img.height = 200;
    img.src = entry.media.media_file_path;

    imgDiv.appendChild(img);
    div.appendChild(imgDiv);
}

function insertAnswerImage(entry) {
    const img = document.createElement("img");

    img.alt = entry.media.description;
    img.width = 300;
    img.height = 200;
    img.src = entry.media.media_file_path;

    imgDiv.appendChild(img);

    return imgDiv;
}

function insertAudio(entry, table, div) {
    const audio = document.createElement("audio");
    const audioStart = createInputElement("hidden", entry.media.media_start, `videoStart-${table}`);
    const audioEnd = createInputElement("hidden", entry.media.media_end, `videoEnd-${table}`);

    audio.id = `audio-${table}`;
    audio.preload = true;
    audio.controls = true;
    audio.ontimeupdate = `${table}Audio()`;


    const source = `<source src="${entry.media.media_file_path}" #t="${entry.media.media_start}" type="audio/mp3">`;

    audio.innerHTML = source;

    div.appendChild(audio);
    div.appendChild(audioStart);
    div.appendChild(audioEnd);
}

function createInputElement(type, value, id) {
    const inputElement = document.createElement("input");
    inputElement.type =  type;
    inputElement.value = value;
    inputElement.id = id;
    return inputElement
}

function insertAnswers(answers, questionDiv) {
    let audVidMedia = null;
    const answerOptions = document.createElement("div");
    answerOptions.classList.add("answersOption");

    answers.forEach((answer, index) => {
        let mediaType = answer.media_type;
        let answerDisplay = "ERROR";
        if (mediaType == "IMG") {
            answerDisplay = insertAnswerImage(answer);
        } else {
            answerDisplay = answer.answer_text;
            if (mediaType == "AUD" || mediaType == "VID" && answer.is_correct) {
                audVidMedia = insertAnswerMedia(answer);
            }
        } 

        let buttonAnswer = document.createElement("button");
        buttonAnswer.classList.add(`answer${index + 1}`);
        buttonAnswer.innerHTML = answerDisplay;
        if (answer.is_correct == 1) {
            buttonAnswer.id = "rightPlayAnswer";
            
        } else {
            buttonAnswer.classList.add("wrongPlayAnswer");
        }

        answerOptions.appendChild(buttonAnswer);
    });

    if (audVidMedia != null) {
        questionDiv.appendChild(audVidMedia);
    }
    questionDiv.appendChild(answerOptions);
}

BuildPage();