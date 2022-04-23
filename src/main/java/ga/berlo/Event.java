package ga.berlo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Event {
    @NonNull
    private UUID id;
    private LocalDateTime timeTag;
    private String desription;
}
