/**
 * Copyright (c) 2012-2017, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.redhat.xpaas.logger;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@SuppressWarnings({
    "PMD.VariableNamingConventions", "PMD.RedundantFieldInitializer"
})
public @interface Loggable {

    /**
     * TRACE level of logging.
     */
    int TRACE = 0;

    /**
     * DEBUG level of logging.
     */
    int DEBUG = 1;

    /**
     * INFO level of logging.
     */
    int INFO = 2;

    /**
     * WARN level of logging.
     */
    int WARN = 3;

    /**
     * ERROR level of logging.
     */
    int ERROR = 4;

    /**
     * Level of logging.
     */
    int value() default com.redhat.xpaas.logger.Loggable.INFO;

    /**
     * Maximum amount allowed for this method (a warning will be
     * issued if it takes longer).
     * @since 0.7.6
     */
    int limit() default 1;

    /**
     * Time unit for the limit.
     * @since 0.7.14
     */
    TimeUnit unit() default TimeUnit.MINUTES;

    /**
     * Shall we trim long texts in order to make log lines more readable?
     * @since 0.7.13
     */
    boolean trim() default true;

    /**
     * Method entry moment should be reported as well (by default only
     * an exit moment is reported).
     * @since 0.7.16
     */
    boolean prepend() default false;


    Class<? extends Throwable>[] ignore() default { };

    /**
     * Skip logging of result, replacing it with dots?
     * @since 0.7.19
     */
    boolean skipResult() default false;

    /**
     * Skip logging of arguments, replacing them all with dots?
     * @since 0.7.19
     */
    boolean skipArgs() default false;

    /**
     * Add toString() result to log line.
     * @since 0.8.1
     */
    boolean logThis() default false;

    /**
     * The precision (number of fractional digits) to be used when displaying
     * the measured execution time.
     * @since 0.18
     */
    int precision() default 2;

    /**
     * The name of the logger to be used. If not specified, defaults to the
     * class name of the annotated class or method.
     * @since 0.18
     */
    String name() default "";

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Quiet {
    }

}
