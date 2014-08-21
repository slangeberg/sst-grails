import grails.util.Environment

class BootStrap {

    def init = { servletContext ->
       log.info "init() - running Environment: ${Environment.current}"
       log.info "init() - isDebugLogging: ${log.isDebugEnabled()}, isInfoLogging: ${log.isInfoEnabled()}"

    }
    def destroy = {
    }
}