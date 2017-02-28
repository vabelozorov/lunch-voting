package ua.belozorov.lunchvoting.util.hibernate;

import org.hibernate.QueryException;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Created on 27.02.17.
 */
public class MyHsqlDialect extends HSQLDialect {

    public MyHsqlDialect() {
        super();
        registerFunction("bitwise_and", new MyHsqlDialect.BitwiseAndFunction("bitwise_and"));
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
