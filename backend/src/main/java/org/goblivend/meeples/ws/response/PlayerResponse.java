package org.goblivend.meeples.ws.response;

import lombok.AllArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@AllArgsConstructor
@Schema(name = "PlayerResponse", description = "Player response containing meeples used")
public class PlayerResponse {
    public String name;
    public List<MeepleUse> meeples;

    @AllArgsConstructor
    public static class MeepleUse {
        @Schema(name = "meepleType", description = "Type of meeple used")
        public String meepleType;
        @Schema(name = "count", description = "Number of times the meeple was used in the current turn")
        public Integer count;
        @Schema(name = "used", description = "Whether the meeple is in the used state")
        public boolean used;
    }
}
