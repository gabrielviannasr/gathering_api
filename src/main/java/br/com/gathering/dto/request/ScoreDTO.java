package br.com.gathering.dto.request;

import br.com.gathering.entity.Score;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDTO {

	// Not needed in post method
	// private Long id;

	private Long idRound;

	private Long idPlayer;

	private Long idPlayerKilledBy;

	private Boolean isDead;

    private String primaryCommanderName;

    private Integer primaryCommanderCount;

    private String secondaryCommanderName;

    private Integer secondaryCommanderCount;

    private Integer infectCount;

    private Integer lifeCount;

    public Score toModel() {
        Score roundPlayer = new Score();
//        roundPlayer.setId(this.id);
        roundPlayer.setIdRound(this.idRound);
        roundPlayer.setIdPlayer(this.idPlayer);
//        roundPlayer.setIdPlayerKilledBy(this.idPlayerKilledBy);
//        roundPlayer.setIsDead(this.isDead);
//        roundPlayer.setPrimaryCommanderName(this.primaryCommanderName);
//        roundPlayer.setPrimaryCommanderCount(this.primaryCommanderCount);
//        roundPlayer.setSecondaryCommanderName(this.secondaryCommanderName);
//        roundPlayer.setSecondaryCommanderCount(this.secondaryCommanderCount);
//        roundPlayer.setInfectCount(this.infectCount);
//        roundPlayer.setLifeCount(this.lifeCount);

        return roundPlayer;
    }

	@Override
	public String toString() {
	    return "RoundPlayerDTO: {\n"
//	            + "\tid: " + this.id + ",\n"
	            + "\tidRound: " + this.idRound + ",\n"
	            + "\tidPlayer: " + this.idPlayer + ",\n"
//	            + "\tidPlayerKilledBy: " + this.idPlayerKilledBy + ",\n"
//	            + "\tisDead: " + this.isDead + ",\n"
//	            + "\tprimaryCommanderName: " + this.primaryCommanderName + ",\n"
//	            + "\tprimaryCommanderCount: " + this.primaryCommanderCount + ",\n"
//	            + "\tsecondaryCommanderName: " + this.secondaryCommanderName + ",\n"
//	            + "\tsecondaryCommanderCount: " + this.secondaryCommanderCount + ",\n"
//	            + "\tinfectCount: " + this.infectCount + ",\n"
//	            + "\tlifeCount: " + this.lifeCount + ",\n"
	            + "}";
	}

}
