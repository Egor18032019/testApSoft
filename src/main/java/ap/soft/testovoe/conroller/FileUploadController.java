package ap.soft.testovoe.conroller;


import ap.soft.testovoe.utils.Const;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@RestController
public class FileUploadController {


    @PostMapping(value = Const.UPLOAD_URL)
    public ResponseEntity<List<List<String>>> upload(@RequestBody byte[] file) {
        List<String> part = new ArrayList<>();
        List<List<String>> answer = new ArrayList<>();
        String decodingStringFromByteArray = new String(file, StandardCharsets.UTF_8);
        String[] s4 = decodingStringFromByteArray.split("\r\n");
        int delimiterCounter = 0;
        int answerCounter = 0;
        int workedInformationLength = 4;
        for (int i = workedInformationLength; i < s4.length - 1; i++) {
            String text = s4[i].trim();
            if (!text.isEmpty()) {
                if (text.startsWith(String.valueOf(Const.DELIMITER))) {
                    if (!text.startsWith(String.valueOf(Const.DELIMITER), 1)) {
                        // если первый # а второй не #
                        System.out.println(text);
                        System.out.println("где ???");
                        if (answerCounter == 0) {
                            // начало заполнение ответа
                            part.add(delimiterCounter, text);
                            delimiterCounter = delimiterCounter + 1;
                            answer.add(answerCounter, part);
                            answerCounter = answerCounter + 1;
                        } else {
                            part = new ArrayList<>();
                            delimiterCounter = 0;
                            part.add(delimiterCounter, text);
                            delimiterCounter = delimiterCounter + 1;

                            answer.add(answerCounter, part);
                            answerCounter = answerCounter + 1;
                        }
                    } else {
                        //  если начинается на # и следующий #
                        // проверяем сколько # стоит в начале
                        int charsInBeginningLine = 0;
                        for (int s = 0; s < text.length(); s++) {
                            char c = text.charAt(s);
                            boolean isDelimiter = c == Const.DELIMITER;
                            if (isDelimiter) {
                                charsInBeginningLine++;
                            }
                        }
                        System.out.println(" charsInBeginningLine " + charsInBeginningLine);
                        System.out.println(" delimiterCounter " + delimiterCounter);
                        // и если больше чем delimiterCounter
                        // то от счетчика отнимаем один вставляем в массив и после этого увеличиваем счетчик
                        if (charsInBeginningLine < delimiterCounter) {
//                       TODO из ТЗ не ясно может ли быть ситуация где в 11 строке #### а в 12 строке ##
// и если такая ситуация возможно то как должна быть сортировка после этого ?
                            part.add(charsInBeginningLine, text.strip());
                            delimiterCounter = delimiterCounter + 1;
                        } else {
                            part.add(delimiterCounter, text.strip());
                            delimiterCounter = delimiterCounter + 1;

                        }
                    }
                } else {
                    // если просто перенос строки без разделителя то добавляем его к предыдущему разделителю
                    int lastIndex = delimiterCounter - 1;
                    String foo = part.get(lastIndex) + " " + text.strip();
                    part.set(lastIndex, foo);
                }
            } else {
                System.out.println("Пустые строки нам ненужны !");
            }
        }

        System.out.println(answer.toString());
        return ResponseEntity.status(HttpStatus.OK)
                .body(answer);
    }
}
