
function insertQuestionMedia(question) {
    const mediaDiv = document.createElement("div");
    mediaDiv.id = "questionMedia";

    const imgDiv = document.createElement("div");
    imgDiv.classList.add("imgWrap");

    console.log(question.question_type);

    switch (question.question_type) {
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

    switch (answer.answer_type) {
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

    console.log(div);
}

function insertImage(entry, div) {

    const img = document.createElement("img");

    img.alt = entry.media.description;
    img.width = 300;
    img.height = 200;
    img.src = entry.media.media_file_path;

    div.appendChild(img);
}

function insertAnswerImage(entry) {
    const img = document.createElement("img");
    const imgDiv = document.createElement("div");
    imgDiv.classList.add("imgWrap");

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
        let mediaType = answer.answer_type;
        console.log("MediaType" + mediaType);

        let buttonAnswer = document.createElement("button");
        buttonAnswer.classList.add(`answer${index + 1}`);


        let answerDisplay = "ERROR";
        if (mediaType === "IMG") {
            answerDisplay = insertAnswerImage(answer);
            buttonAnswer.appendChild(answerDisplay);
        } else {
            answerDisplay = answer.answer_text;
            buttonAnswer.innerHTML = answerDisplay;
            if (mediaType === "AUD" || mediaType === "VID" && answer.is_correct) {
                audVidMedia = insertAnswerMedia(answer);
            }
        } 


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

