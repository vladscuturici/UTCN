module Model.Event.Category exposing (EventCategory(..), SelectedEventCategories, allSelected, eventCategories, isEventCategorySelected, set, view)

import Html exposing (Html, div, input, text)
import Html.Attributes exposing (checked, class, style, type_)
import Html.Events exposing (onCheck)


type EventCategory
    = Academic
    | Work
    | Project
    | Award


eventCategories =
    [ Academic, Work, Project, Award ]


{-| Type used to represent the state of the selected event categories
-}
type alias SelectedEventCategories
    = List EventCategory


{-| Returns an instance of `SelectedEventCategories` with all categories selected

    isEventCategorySelected Academic allSelected --> True

-}
allSelected : SelectedEventCategories
allSelected =
    eventCategories
    -- Debug.todo "Implement Model.Event.Category.allSelected"

{-| Returns an instance of `SelectedEventCategories` with no categories selected

-- isEventCategorySelected Academic noneSelected --> False

-}
noneSelected : SelectedEventCategories
noneSelected =
    []
    -- Debug.todo "Implement Model.Event.Category.noneSelected"

{-| Given a the current state and a `category` it returns whether the `category` is selected.

    isEventCategorySelected Academic allSelected --> True

-}
isEventCategorySelected : EventCategory -> SelectedEventCategories -> Bool
isEventCategorySelected category selectedCategories =
    List.member category selectedCategories
    -- Debug.todo "Implement Model.Event.Category.isEventCategorySelected"


{-| Given an `category`, a boolean `value` and the current state, it sets the given `category` in `current` to `value`.

    allSelected |> set Academic False |> isEventCategorySelected Academic --> False

    allSelected |> set Academic False |> isEventCategorySelected Work --> True

-}
set : EventCategory -> Bool -> SelectedEventCategories -> SelectedEventCategories
set category value current =
    case value of
        True ->
            if List.member category current then
                current
            else
                category :: current
        False ->
            List.filter ((/=) category) current
    -- Debug.todo "Implement Model.Event.Category.set"


checkbox : String -> Bool -> EventCategory -> Html ( EventCategory, Bool )
checkbox name state category =
    div [ style "display" "inline", class "category-checkbox" ]
        [ input [ type_ "checkbox", onCheck (\c -> ( category, c )), checked state ] []
        , text name
        ]

eventCategoryToString : EventCategory -> String
eventCategoryToString category =
    case category of
        Academic -> "Academic"
        Work -> "Work"
        Project -> "Project"
        Award -> "Award"

view : SelectedEventCategories -> Html ( EventCategory, Bool )
view selectedCategories =
    div []
        ( List.map (\category -> 
            checkbox (eventCategoryToString category) (isEventCategorySelected category selectedCategories) category) 
          eventCategories )
    -- Debug.todo "Implement the Model.Event.Category.view function"
