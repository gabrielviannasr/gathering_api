package br.com.gathering.dto.request;

import br.com.gathering.entity.Gathering;
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
public class GatheringDTO {

	// Not needed in post method
	// private Long id;

	private Integer year;

    private String name;
    
	public void init() {
	}

	public Gathering toModel() {
		Gathering gathering = new Gathering();
		gathering.setYear(this.year);
		gathering.setName(this.name);

		return gathering;
	}

    @Override
    public String toString() {
		return "GatheringDTO: {\n"
//				+ "\tid: " + this.id + ",\n"
				+ "\tyear: " + this.year + ",\n"
				+ "\tname: " + this.name + ",\n"
				+ "}";
    }

}
