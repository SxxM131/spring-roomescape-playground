package roomescape.controller;

import java.net.URI;
import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> reservationRowMapper = (resultSet, rowNum) -> new Reservation(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("date"),
            resultSet.getString("time")
    );

    public ReservationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @GetMapping("/reservations")
    @ResponseBody
    public List<Reservation> reservations() {
        return jdbcTemplate.query("SELECT id, name, date, time FROM reservation", reservationRowMapper);
    }

    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Reservation> create(@RequestBody Reservation request) {
        if (isBlank(request.getName()) || isBlank(request.getDate()) || isBlank(request.getTime())) {
            throw new BadRequestReservationException("예약자, 날짜, 시간을 모두 입력해야 합니다.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO reservation(name, date, time) VALUES (?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, request.getName());
            ps.setString(2, request.getDate());
            ps.setString(3, request.getTime());
            return ps;
        }, keyHolder);

        Reservation reservation = new Reservation(
                keyHolder.getKey().longValue(),
                request.getName(),
                request.getDate(),
                request.getTime()
        );

        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        int deletedCount = jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id);
        if (deletedCount == 0) {
            throw new NotFoundReservationException("존재하지 않는 예약입니다. id=" + id);
        }
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
