package canve.sbt

import sbt.Keys._
import sbt._

import org.canve.compilerPlugin.Normalize

// TODO: add cleanup as per http://www.scala-sbt.org/0.13.5/docs/Getting-Started/More-About-Settings.html#appending-with-dependencies-and

object Plugin extends AutoPlugin {
  
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements
  
  val compilerPluginOrg = "canve"
  val compilerPluginVersion = "0.0.1"
  val compilerPluginArtifact = "compiler-plugin"
  val compilerPluginNameProperty = "canve" // this is defined in the compiler plugin's code
    
  val sbtCommandName = "canve"

  // see: https://github.com/sbt/sbt/issues/1095 or https://github.com/sbt/sbt/issues/780
  val aggregateFilter: ScopeFilter.ScopeFilter = ScopeFilter( inAggregates(ThisProject), inConfigurations(Compile) )

  //val canveCommand: Command = Command(sbtCommandName)(){ ()}

  // global settings needed for the bootstrap
  override lazy val projectSettings = Seq(
    commands += Command.command(sbtCommandName,
                                "Instruments all projects in the current build definition such that they run canve during compilation",
                                "Instrument all projects in the current build definition such that they run canve during compilation")
                                (go()),

    libraryDependencies += compilerPluginOrg % (compilerPluginArtifact + "_" + scalaBinaryVersion.value) % compilerPluginVersion % "provided"
    
  )

  private def go(): State => State = { state =>
    
    org.canve.util.CanveDataIO.clearAll
        
    val extracted: Extracted = Project.extract(state)

    val newSettings: Seq[Def.Setting[Task[Seq[String]]]] = extracted.structure.allProjectRefs map { projRef =>
      val projectName = projRef.project
      //println("canve instrumenting project " + projectName)
      
      lazy val pluginScalacOptions: Def.Initialize[Task[Seq[String]]] = Def.task {
        // search for the compiler plugin
        val deps: Seq[File] = update.value matching configurationFilter("provided")
        deps.find(_.getAbsolutePath.contains(compilerPluginArtifact)) match {
          case None => throw new Exception(s"Fatal: compilerPluginArtifact not in libraryDependencies")
          case Some(pluginPath) => 
            Seq(
                
              Some(s"-Yrangepos"),                                             // enables obtaining accurate source ranges in the compiler plugin
                                                                               // but will crash with some scala 2.10 projects using macros (c.f. https://github.com/scoverage/scalac-scoverage-plugin/blob/master/2.10.md)
              Some(s"-Xplugin:${pluginPath.getAbsolutePath}"),                 // hooks in the compiler plugin
              
              Some(s"-P:$compilerPluginNameProperty:projectName:$projectName") // passes the project name
              
            ).flatten
        }
      }
      scalacOptions in projRef ++= pluginScalacOptions.value
    }
    val appendedState = extracted.append(newSettings, state)

    val structure = extracted.structure

    val successfulProjects = (for (projRef <- extracted.structure.allProjectRefs.toStream) yield {
      EvaluateTask(structure, clean, appendedState, projRef) match {
        case None =>
          throw new Exception("sbt plugin internal error - failed to evaluate the clean task")
        case _ =>
          EvaluateTask(structure, compile in Test, appendedState, projRef) match {
            case None =>
              throw new Exception("sbt plugin internal error - failed to evaluate the compile task")
            case Some((resultState, result)) => result.toEither match {
              case Left(incomplete: Incomplete) =>
                false
              case Right(analysis) =>
                true
            }
            case _ => throw new Exception("sbt plugin internal error - unexpected result from sbt api")
          }
      }
    }).takeWhile(_ == true).force

    successfulProjects.length == extracted.structure.allProjectRefs.length match {
      case true =>
        new Normalize
        println("canve task done")
        state
      case false =>
        println("canve task aborted as it could not successfully compile the project (or due to its own internal error)")
        state.fail
    }
  }
  
  println(s"[canve] sbt canve plugin loaded - enter the command `$sbtCommandName` in sbt, to run canve for your project")
}
