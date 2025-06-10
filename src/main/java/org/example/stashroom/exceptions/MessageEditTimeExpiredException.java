package org.example.stashroom.exceptions;

public class MessageEditTimeExpiredException extends RuntimeException {
    public MessageEditTimeExpiredException() {
        super("Редактировать сообщение можно только в течение 24 часов после отправки");
    }

    public MessageEditTimeExpiredException(String message) {
        super(message);
    }
}

