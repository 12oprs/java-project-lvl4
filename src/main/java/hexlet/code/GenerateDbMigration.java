package hexlet.code;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import java.io.IOException;

public class GenerateDbMigration {

    //@param args - standart parameter for main()
    public static void main(String[] args) throws IOException {
        org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
        DbMigration dbMigration = DbMigration.create();
        dbMigration.addPlatform(Platform.POSTGRES, "postgres");
        dbMigration.addPlatform(Platform.H2, "h2");
        dbMigration.generateMigration();
    }

}
