package repository;

import swimapp.domain.Round;


public interface RoundRepository  extends ICrudRepository<Integer, Round>{
     int getNumberOfParticipants(Integer idRound);
     void addSwimRound(Integer idSwimmer, Integer idRound);
}
