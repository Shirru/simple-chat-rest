package simplechat.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import simplechat.error.*;
import simplechat.error.Error;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(ChatUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error chatUserNotFound(ChatUserNotFoundException e) {
        Long chatUserId = e.getChatUserId();
        return new Error(4, "ChatUser [" + chatUserId + "] not found");
    }

    @ExceptionHandler(ChatUserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error chatUserAlreadyExist(ChatUserAlreadyExistException e) {
        return new Error(3, "ChatUser already exist");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Error accessDenied(BadCredentialsException e) {
        return new Error(2, "Access denied for user [" + e.getMessage() + "]");
    }

    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error contactNotFound(ContactNotFoundException e) {
        return new Error(1, "Contact [" + e.getPhone() + "] not found");
    }

    @ExceptionHandler(ContactAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error contactAlreadyExist(ContactAlreadyExistException e) {
        return new Error(0, "Contact [" + e.getPhone() + "] already exist");
    }
}
