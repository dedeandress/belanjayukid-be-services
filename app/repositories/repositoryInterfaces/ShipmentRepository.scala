package repositories.repositoryInterfaces

import models.Shipment

trait ShipmentRepository {

  def addShipment(shipment: Shipment)

}
