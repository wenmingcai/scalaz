package scalaz

////
/**
 *
 */
////
trait MonadError[F[_], S] extends Monad[F] { self =>
  ////

  def raiseError[A](e: S): F[A]
  def handleError[A](fa: F[A])(f: S => F[A]): F[A]

  trait MonadErrorLaw {
    def raisedErrorsHandled[A](e: S, f: S => F[A])(implicit FEA: Equal[F[A]]): Boolean =
      FEA.equal(handleError(raiseError(e))(f), f(e))
    def errorsRaised[A](a: A, e: S)(implicit FEA: Equal[F[A]]): Boolean =
      FEA.equal(bind(point(a))(_ => raiseError(e)), raiseError(e))
    def errorsStopComputation[A](e: S, a: A)(implicit FEA: Equal[F[A]]): Boolean =
      FEA.equal(bind(raiseError(e))(point), raiseError(e))
  }
  def monadErrorLaw = new MonadErrorLaw {}

  ////
  val monadErrorSyntax = new scalaz.syntax.MonadErrorSyntax[F, S] { def F = MonadError.this }
}

object MonadError {
  @inline def apply[F[_], S](implicit F: MonadError[F, S]): MonadError[F, S] = F

  ////
  import Isomorphism.<~>

  def fromIso[F[_], G[_], E](D: F <~> G)(implicit ME: MonadError[G, E]): MonadError[F, E] =
    new IsomorphismMonadError[F, G, E] {
      override implicit def G: MonadError[G, E] = ME
      override def iso: F <~> G = D
    }

  ////
}
