package repository;

import swimapp.domain.Round;
import swimapp.domain.Styles;
import swimapp.domain.Swimmer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RoundJdbcRepository implements RoundRepository {
    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public RoundJdbcRepository(Properties props){
        logger.info("Initializing SortingTaskRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from round")) {
            try(ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    logger.traceExit(result.getInt("SIZE"));
                    return result.getInt("SIZE");
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return 0;
    }

    @Override
    public void save(Round entity) {
        logger.traceEntry("saving task {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into round (distance, style) values (?,?)")){
            preStmt.setInt(1,entity.getDistance());
            preStmt.setString(2,entity.getStyle().toString());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Integer integer) {
        logger.traceEntry("deleting task with {}",integer);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from round where id=?")){
            preStmt.setInt(1,integer);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Round entity) {

    }


    @Override
    public Round findOne(Integer integer) {
        logger.traceEntry("finding task with id {} ",integer);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from round where id=?")){
            preStmt.setInt(1,integer);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("id");
                    int distance = result.getInt("distance");
                    Styles style = Styles.valueOf(result.getString("style"));
                    Round round = new Round(distance, style);
                    round.setId(id);
                    logger.traceExit(round);
                    return round;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit("No task found with id {}", integer);

        return null;
    }

    @Override
    public Iterable<Round> findAll() {
        //logger.();
        Connection con=dbUtils.getConnection();
        List<Round> rounds=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from round")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    int distance = result.getInt("distance");
                    Styles style = Styles.valueOf(result.getString("style"));
//                    SortingOrder order = SortingOrder.valueOf(result.getString("orderC"));
//                    SortingAlgorithm algo = SortingAlgorithm.valueOf(result.getString("algoritm"));
//                    SortingTask task = new SortingTask(id, desc, algo, order, elems);
                    Round round = new Round(distance, style); round.setId(id);
                    rounds.add(round);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(rounds);
        return rounds;
    }

    @Override
    public int getNumberOfParticipants(Integer idRound) {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from swimmround where idround=?")) {
            preStmt.setInt(1,idRound);
            try(ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    logger.traceExit(result.getInt("SIZE"));
                    return result.getInt("SIZE");
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return 0;
    }

    @Override
    public void addSwimRound(Integer idSwimmer, Integer idRound) {
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into swimmround (idswimmer, idround) values (?,?)")){
            preStmt.setInt(1, idSwimmer);
            preStmt.setInt(2, idRound);
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }


}
