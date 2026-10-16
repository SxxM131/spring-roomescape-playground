package roomescape.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;

@Controller
public class ReservationController {

    private List<Reservation> reservations = new ArrayList<>();

    // 3단계에서 삭제: 화면 확인용 임시 데이터
    public ReservationController() {
        reservations.add(new Reservation(1L, "브라운", "2023-01-01", "10:00"));
        reservations.add(new Reservation(2L, "브라운", "2023-01-02", "11:00"));
        reservations.add(new Reservation(3L, "브라운", "2023-01-03", "12:00"));
    }

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
}
