module Model.Event exposing (..)

import Html exposing (..)
import Html.Attributes exposing (class, classList, href)
import Model.Event.Category exposing (EventCategory(..))
import Model.Interval as Interval exposing (Interval)


type alias Event =
    { title : String
    , interval : Interval
    , description : Html Never
    , category : EventCategory
    , url : Maybe String
    , tags : List String
    , important : Bool
    }


categoryView : EventCategory -> Html Never
categoryView category =
    case category of
        Academic ->
            text "Academic"

        Work ->
            text "Work"

        Project ->
            text "Project"

        Award ->
            text "Award"


sortByInterval : List Event -> List Event
sortByInterval events =
    List.sortWith (\event1 event2 -> Interval.compare event1.interval event2.interval) events
    -- Debug.todo "Implement Event.sortByInterval"


view : Event -> Html Never
view event =
    div 
        [ classList 
            [ ("event", True), ("event-important", event.important)
            ]
        ]
        [ h2 [ class "event-title" ] [ text event.title ], 
          div [ class "event-description" ] [ event.description ], 
          div [ class "event-category" ] [ categoryView event.category ], 
          div [ class "event-interval" ] [], 
          case event.url of
            Nothing ->
                text ""
            Just url ->
                a [ class "event-url", href url ] [ text "Link" ]
        ]
    -- Debug.todo "Implement the Model.Event.view function"
