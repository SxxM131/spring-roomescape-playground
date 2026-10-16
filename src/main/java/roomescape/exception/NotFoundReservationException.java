package roomescape.exception;

// 삭제하려는 예약이 없을 때 던지는 예외 (→ 404)
public class NotFoundReservationException extends RuntimeException {

    public NotFoundReservationException(String message) {
        super(message);
    }
}
