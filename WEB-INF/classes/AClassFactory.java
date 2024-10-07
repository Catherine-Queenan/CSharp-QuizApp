public class AClassFactory {
    public AClass createAClass(String tableType, String constructorParameters) {
        if(tableType.equalsIgnoreCase("category")){
            return new Category(constructorParameters);
        } else if(tableType.equalsIgnoreCase("quiz")){
            return new Quiz(constructorParameters);
        } else if(tableType.equalsIgnoreCase("question")){
            return new Question(constructorParameters);
        } else if(tableType.equalsIgnoreCase("answer")){
            return new Answer(constructorParameters);
        } else if(tableType.equalsIgnoreCase("media")){
            return new Media(constructorParameters);
        } else  if(tableType.equalsIgnoreCase("websocket")){
            return new WebSocketQuestion(constructorParameters);
        }
        return null;
    }
}
