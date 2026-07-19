package br.com.gathering.dto.request;

import br.com.gathering.entity.Player;
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
public class PlayerDTO {

	// Not needed in post method
	// private Long id;

    private String name;

    private String username;

    private String email;

    private String password;

    private Double wallet;

	public Player toModel() {
	    Player player = new Player();
//	    player.setId(this.id);
	    player.setName(this.name);
//	    player.setUsername(this.username);
//	    player.setEmail(this.email);
//	    player.setPassword(this.password);
//	    player.setWallet(this.wallet);

	    return player;
	}

    @Override
    public String toString() {
		return "PlayerDTO: {\n"
//				+ "\tid: " + this.id + ",\n"
				+ "\tname: " + this.name + ",\n"
//				+ "\tusername: " + this.username + ",\n"
//				+ "\tpassword: " + this.password + ",\n"
//				+ "\twallet: " + this.wallet + ",\n"
				+ "}";
    }

}
