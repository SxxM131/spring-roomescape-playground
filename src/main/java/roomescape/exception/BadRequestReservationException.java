package roomescape.exception;

// 예약 요청 값이 잘못됐을 때 던지는 예외 (→ 400)
public class BadRequestReservationException extends RuntimeException {

    public BadRequestReservationException(String message) {
        super(message);
    }
}
