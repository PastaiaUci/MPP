package repository;

//import com.sun.java.accessibility.util.SwingEventMonitor;
import swimapp.domain.Round;
import swimapp.domain.Seller;
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

/**
 * Created by grigo on 3/2/17.
 */
public class SwimJdbcRepository implements SwimRepository {
    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public SwimJdbcRepository(Properties props){
        logger.info("Initializing SortingTaskRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from swimmer")) {
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
    public void save(Swimmer entity) {
        logger.traceEntry("saving task {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into swimmer (name, age) values (?,?)")) {
            preStmt.setString(1, entity.getName());
            preStmt.setInt(2, entity.getAge());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Integer integer) {
        logger.traceEntry("deleting task with {}",integer);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from swimmer where idswimmer=?")){
            preStmt.setInt(1,integer);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Swimmer entity) {

    }


    @Override
    public Swimmer findOne(Integer integer) {
        logger.traceEntry("finding task with id {} ",integer);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from swimmer where idswimmer=?")){
            preStmt.setInt(1,integer);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("idswimmer");
                    String name = result.getString("name");
                    int age = result.getInt("age");
//                    SortingOrder order = SortingOrder.valueOf(result.getString("orderC"));
//                    SortingAlgorithm algo = SortingAlgorithm.valueOf(result.getString("algoritm"));
//                    SortingTask task = new SortingTask(id, desc, algo, order, elems);
                    Swimmer swimmer = new Swimmer(name, age);
                    swimmer.setId(id);
                    logger.traceExit(swimmer);
                    return swimmer;
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
    public Iterable<Swimmer> findAll() {
        //logger.();
        Connection con=dbUtils.getConnection();
        List<Swimmer> tasks=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from swimmer")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("idswimmer");
                    String name = result.getString("name");
                    int age = result.getInt("age");
//                    SortingOrder order = SortingOrder.valueOf(result.getString("orderC"));
//                    SortingAlgorithm algo = SortingAlgorithm.valueOf(result.getString("algoritm"));
//                    SortingTask task = new SortingTask(id, desc, algo, order, elems);
                    Swimmer swimmer = new Swimmer(name, age); swimmer.setId(id);
                    tasks.add(swimmer);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(tasks);
        return tasks;
    }

    public Seller checkLogIn(String email, String password) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        Seller seller = null;
        try (PreparedStatement preStmt = con.prepareStatement("select * from seller where email = ? and password = ?")) {
            preStmt.setString(1, email);
            preStmt.setString(1, password);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Integer idseller = result.getInt(1);
                    String emailu = result.getString(2);
                    String passwordu = result.getString(3);

                    seller = new Seller(emailu, passwordu);
                    seller.setId(idseller);
                    return seller;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return seller;
    }

    public Swimmer getLastRecord(){
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("SELECT * FROM swimmer ORDER BY idswimmer DESC LIMIT 1;")){
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    int id = result.getInt("idswimmer");
                    String name = result.getString("name");
                    int age = result.getInt("age");
                    Swimmer swimmer = new Swimmer(name, age);
                    swimmer.setId(id);
                    logger.traceExit(swimmer);
                    return swimmer;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return null;
    }

    public Iterable<Swimmer> getAllSwimmersForRound(Integer roundId){
        Connection con=dbUtils.getConnection();
        List<Swimmer> all=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select s.idswimmer, s.name, s.age from swimmer s inner join swimmround s2 on s.idswimmer = s2.idswimmer where idround = ?")) {
            preStmt.setInt(1, roundId);
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("idswimmer");
                    String name = result.getString("name");
                    int age = result.getInt("age");
                    Swimmer swimmer = new Swimmer(name, age); swimmer.setId(id);
                    all.add(swimmer);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(all);
        return all;
    }

    public Iterable<Round> getAllRoundForSwimmer(Integer idSwimmer){
        Connection con=dbUtils.getConnection();
        List<Round> all=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select distinct r.id, r.distance, r.style from round r inner join swimmround sr on r.id = sr.idround inner join swimmer s on sr.idswimmer = s.idswimmer where s.idswimmer=?")) {
            preStmt.setInt(1, idSwimmer);
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    Integer distance = result.getInt("distance");
                    Styles style = Styles.valueOf(result.getString("style"));
                    Round round = new Round(distance, style);
                    round.setId(id);
                    all.add(round);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(all);
        return all;
    }
}
