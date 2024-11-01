package diplome;

import lombok.Data;

//Класс юзается ддля дессериализации появляется во многих отватах запросов
@Data
public class Message {
    private boolean success;
    private String message;


}
