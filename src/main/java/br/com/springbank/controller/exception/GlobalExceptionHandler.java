package br.com.springbank.controller.exception;

import br.com.springbank.service.exceptions.account.AccountNotFoundException;
import br.com.springbank.service.exceptions.account.AccountNumberGenerationException;
import br.com.springbank.service.exceptions.account.UserAccountNotFoundException;
import br.com.springbank.service.exceptions.email.EmailMessageRequiredException;
import br.com.springbank.service.exceptions.email.EmailRecipientRequiredException;
import br.com.springbank.service.exceptions.email.EmailSendingException;
import br.com.springbank.service.exceptions.email.EmailTitleRequiredException;
import br.com.springbank.service.exceptions.token.InvalidOrExpiredTokenException;
import br.com.springbank.service.exceptions.token.InvalidTokenSubjectException;
import br.com.springbank.service.exceptions.transaction.InsufficientBalanceException;
import br.com.springbank.service.exceptions.transaction.InvalidTransactionAmountException;
import br.com.springbank.service.exceptions.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAccountNotFoundException.class)
    public ResponseEntity<StandardError> userAccountNotFound(UserAccountNotFoundException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<StandardError> accountNotFound(AccountNotFoundException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UsernameRequiredException.class)
    public ResponseEntity<StandardError> usernameRequired(UsernameRequiredException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardError> userNotFound(UserNotFoundException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UserAlreadyInactiveException.class)
    public ResponseEntity<StandardError> userAlreadyInactive(UserAlreadyInactiveException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(EmailRecipientRequiredException.class)
    public ResponseEntity<StandardError> emailRecipientRequired(EmailRecipientRequiredException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EmailTitleRequiredException.class)
    public ResponseEntity<StandardError> emailTitleRequired(EmailTitleRequiredException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EmailMessageRequiredException.class)
    public ResponseEntity<StandardError> emailMessageRequired(EmailMessageRequiredException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<StandardError> emailSending(EmailSendingException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(InvalidOrExpiredTokenException.class)
    public ResponseEntity<StandardError> invalidOrExpiredToken(InvalidOrExpiredTokenException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(InvalidTokenSubjectException.class)
    public ResponseEntity<StandardError> invalidTokenSubject(InvalidTokenSubjectException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<StandardError> inactiveUser(InactiveUserException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<StandardError> invalidCredentials(InvalidCredentialsException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<StandardError> roleNotFound(RoleNotFoundException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccountNumberGenerationException.class)
    public ResponseEntity<StandardError> accountNumberGeneration(AccountNumberGenerationException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(InvalidTransactionAmountException.class)
    public ResponseEntity<StandardError> invalidTransactionAmount(InvalidTransactionAmountException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<StandardError> insufficientBalance(InsufficientBalanceException ex, WebRequest request) {
        return this.buildErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    private ResponseEntity<StandardError> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        StandardError err = new StandardError(
                ex.getMessage(),
                status.value(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(status).body(err);
    }
}
