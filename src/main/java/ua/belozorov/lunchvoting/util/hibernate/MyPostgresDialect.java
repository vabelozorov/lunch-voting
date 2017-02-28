package ua.belozorov.lunchvoting.util.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.List;

/**

 *
 * Created on 15.02.17.
 */

public class MyPostgresDialect extends org.hibernate.dialect.PostgreSQL94Dialect {

    public MyPostgresDialect() {
        super();
        registerFunction("bitand", new BitwiseAndFunction("bitand"));
    }


    public class BitwiseAndFunction extends StandardSQLFunction implements SQLFunction {

        private BitwiseAndFunction(String bitwise_and) {
            super(bitwise_and);
        }

        @Override
        public String render(Type firstArgument, List args, SessionFactoryImplementor factory)
                throws QueryException {
            if (args.size() != 2) {
                throw new IllegalArgumentException("the function must be passed 2 arguments");
            }
            return String.format("%s & %s", args.get(0), args.get(1));
        }
    }
}