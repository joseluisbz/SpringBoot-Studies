package org.bz.app.mspeople.util;

import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.exceptions.DefaultInternalServerErrorException;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class FunctionsUtil {

    public static Function<? super Stream<StackWalker.StackFrame>, ? extends String> getMethodNameFunction = inputStreamStackFrame -> {
        Optional<StackWalker.StackFrame> optionalStackFrame = inputStreamStackFrame.findFirst();
        return optionalStackFrame.map(StackWalker.StackFrame::getMethodName).orElse("");
    };

    public static Function<? super Stream<StackWalker.StackFrame>, ? extends Optional<StackWalker.StackFrame>> optionalStackFrameFunction = inputStreamStackFrame -> {
        Optional<StackWalker.StackFrame> outputOptionalStackFrame = inputStreamStackFrame.findFirst();
        log.trace("outputOptionalStackFrame: " + outputOptionalStackFrame);
        return outputOptionalStackFrame;
    };

    public static Function<? super Stream<StackWalker.StackFrame>, ? extends StackWalker.StackFrame> stackFrameFunction = inputStreamStackFrame -> {
        Optional<StackWalker.StackFrame> optionalStackFrame = inputStreamStackFrame.findFirst();
        StackWalker.StackFrame outputStackFrame = optionalStackFrame.orElse(null);
        log.trace("outputStackFrame: " + outputStackFrame);
        return outputStackFrame;
    };

    public static BiFunction<Class<?>, StackWalker.StackFrame, String> getClassNameBiFunction =
            (clazz, stackFrame) -> clazz != null ?
                    clazz.getName() :
                    (stackFrame != null ? stackFrame.getClassName() : "");


    public static BiFunction<StackTraceElement[], String, StackTraceElement> getStackTraceElementByClassNameBiFunction =
            (arrayStackTraceElement, className) -> {
                StackTraceElement outputStackTraceElement = null;
                for (int i = arrayStackTraceElement.length - 1; i >= 0; i--) {
                    if (arrayStackTraceElement[i].getClassName().equals(className)) {
                        outputStackTraceElement = arrayStackTraceElement[i];
                        break;
                    }
                }
                return outputStackTraceElement;
            };


    public static Function<DefaultInternalServerErrorException, StackTraceElement> getStackTraceElementByExceptionFunction =
            (defaultInternalServerErrorException) -> {
                String className = getClassNameBiFunction.apply(
                        defaultInternalServerErrorException.getCatcherClass(),
                        defaultInternalServerErrorException.getStackFrame()
                );

                StackTraceElement stackTraceElement = getStackTraceElementByClassNameBiFunction.apply(
                        defaultInternalServerErrorException.getOriginException().getStackTrace(),
                        className
                );
                log.trace("stackTraceElement: " + stackTraceElement);
                return stackTraceElement;
            };
}
