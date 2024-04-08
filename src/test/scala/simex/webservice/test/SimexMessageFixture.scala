package simex.webservice.test

import simex.messaging.Datum
import simex.test.SimexTestFixture
import thediscprog.slogic.Xor

trait SimexMessageFixture extends SimexTestFixture {

  val forename = Datum("forename", None, Xor.applyLeft("John"))
  val surname = Datum("surname", None, Xor.applyLeft("Smith"))
  val house = Datum("address", None, Xor.applyLeft("1 The Street"))
  val town = Datum("town", None, Xor.applyLeft("The Town"))
  val postcode = Datum("postcode", None, Xor.applyLeft("PC01"))
  val address = Datum("address", None, Xor.applyRight(Vector(house, town, postcode)))

  val person = Datum(
    field = "person",
    check = None,
    value = Xor.applyRight(Vector(forename, surname, address))
  )

  val simpleSimexMsg = simexMessage

  val complexSimexMsg = simpleSimexMsg.copy(
    data = Vector(person)
  )
}
