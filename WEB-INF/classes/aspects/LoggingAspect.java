package aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class LoggingAspect {

    @Before("execution(* *.doPost(..))")
    public void beforeDoPostCall(JoinPoint joinPoint) {
        String servletName = joinPoint.getSignature().getDeclaringTypeName();
        System.out.println("Servlet: " + servletName + " - Method: doPost is being called");
    }

    @Before("execution(* *.doGet(..))")
    public void beforeDoGetCall(JoinPoint joinPoint) {
        String servletName = joinPoint.getSignature().getDeclaringTypeName();
        System.out.println("Servlet: " + servletName + " - Method: doGet is being called");
    }
}
