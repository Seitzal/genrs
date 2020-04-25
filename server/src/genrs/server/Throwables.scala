package genrs.server

case object NoAuth extends Throwable
case object BadAuth extends Throwable
case object UserNotFound extends Throwable
case object WrongPassword extends Throwable
