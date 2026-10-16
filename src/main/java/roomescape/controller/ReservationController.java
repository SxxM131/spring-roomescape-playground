package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;
import roomescape.exception.BadRequestReservationException;
import roomescape.exception.NotFoundReservationException;

@Controller
public class ReservationController {

    private List<Reservation> reservations = new ArrayList<>();
    // AtomicLong: 여러 요청이 동시에 와도 id 가 겹치지 않게 1씩 안전하게 증가시켜 준다.
    private AtomicLong index = new AtomicLong(1);

    // 화면용: 뷰 이름을 반환 → templates/reservation.html
    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    // API용: @ResponseBody 가 붙으면 반환값을 뷰 이름이 아니라 응답 본문(JSON)으로 보낸다.
    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> reservations() {
        return reservations;
    }

    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Reservation> create(@RequestBody Reservation request) {
        if (isBlank(request.getName()) || isBlank(request.getDate()) || isBlank(request.getTime())) {
            throw new BadRequestReservationException("예약자, 날짜, 시간을 모두 입력해야 합니다.");
        }

        // getAndIncrement(): 현재 값(1)을 먼저 쓰고 나서 증가 → 첫 id 가 1
        // (incrementAndGet() 은 먼저 증가시키고 반환해서 첫 id 가 2가 된다)
        Reservation reservation = new Reservation(
                index.getAndIncrement(),
                request.getName(),
                request.getDate(),
                request.getTime()
        );
        reservations.add(reservation);

        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // removeIf 는 지운 게 있으면 true, 없으면 false 를 반환한다.
        boolean removed = reservations.removeIf(reservation -> reservation.getId().equals(id));
        if (!removed) {
            throw new NotFoundReservationException("존재하지 않는 예약입니다. id=" + id);
        }
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
