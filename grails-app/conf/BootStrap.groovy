import grails.util.Environment

class BootStrap {

    def init = { servletContext ->
       log.info "init() - running Environment: ${Environment.current}"

    }
    def destroy = {
    }
}