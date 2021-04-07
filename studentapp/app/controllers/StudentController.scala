package controllers

import javax.inject._
import scala.collection.mutable.ListBuffer
import play.api.mvc._
import models.Student
import play.api.libs.json._

@Singleton
class StudentController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  implicit val StudentFormat = Json.format[Student]

  private val studentList = new ListBuffer[Student]()
  studentList += Student(1, "a", "maths")
  studentList += Student(2, "b", "physics")
  studentList += Student(3, "c", "chem")

  def students() = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(studentList)).as("application/json")
  }

  def getStudent(userId: Int) = Action { implicit request: Request[AnyContent] =>

    val foundStudent = studentList.find(_.id == userId)
    foundStudent match {
      case Some(item) => Ok(Json.toJson(item)).as("application/json")
      case None => Ok("not found")
    }
  }

  def addStudent() = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson

    val studentData: Option[Student] =
          jsonObject.flatMap(
            Json.fromJson[Student](_).asOpt
          )

    studentData match {
      case Some(newItem) =>
              val toBeAdded = Student(newItem.id,newItem.name,newItem.course)
              studentList += toBeAdded
              Created(Json.toJson(toBeAdded))
      case None =>
        BadRequest
    }
       //Ok("added")

  }

  def updateStudent(userId : Int) = Action{ implicit request =>
    val content = request.body
    val jsonObject = content.asJson

    val studentData: Option[Student] =
      jsonObject.flatMap(
        Json.fromJson[Student](_).asOpt
      )

    studentData match {
      case Some(newItem) =>
        val toBeAdded = Student(newItem.id,newItem.name,newItem.course)
        studentList.update(userId-1,toBeAdded)
        Created(Json.toJson(toBeAdded))
      case None =>
        BadRequest
    }
  }

  def removeStudent(userId : Int)= Action {

    val foundStudent = studentList.find(_.id == userId)
    studentList.remove(userId-1)
    foundStudent match {
      case Some(item) =>
        Ok(Json.toJson(item))
      case None => Ok("not found")
    }
  }

}
