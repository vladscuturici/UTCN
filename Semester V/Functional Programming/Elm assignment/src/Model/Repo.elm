module Model.Repo exposing (..)

import Html exposing (..)
import Html.Attributes exposing (class, href)
import Json.Decode as De exposing (Decoder)
-- import Json.Decode.Pipeline exposing (required, optional)
import Json.Decode exposing (Decoder, field, string, int, maybe, map5)

type alias Repo =
    { name : String
    , description : Maybe String
    , url : String
    , pushedAt : String
    , stars : Int
    }

repoDescription : Maybe String -> String 
repoDescription description = 
    case description of 
        Nothing -> 
            ""   

        Just desc -> 
            desc

view : Repo -> Html msg
view repo = div[class "repo"][ 
        div [ class "repo-name" ] [ text repo.name ] ,
            div [ class "repo-description" ] [ text (repoDescription repo.description) ],
            a [class "repo-url", href repo.url] [text repo.url],
            div [ class "repo-stars" ] [  ]
        ]


maybeDiv : String -> Maybe String -> Html msg
maybeDiv className maybeContent =
    case maybeContent of
        Just content ->
            div [ class className ] [ text content ]

        Nothing ->
            text ""


sortByStars : List Repo -> List Repo
sortByStars repos =
    List.sortBy .stars repos |> List.reverse
    -- Debug.todo "Implement Model.Repo.sortByStars"


{-| Deserializes a JSON object to a `Repo`.
Field mapping (JSON -> Elm):

  - name -> name
  - description -> description
  - html\_url -> url
  - pushed\_at -> pushedAt
  - stargazers\_count -> stars

-}
decodeRepo : Decoder Repo
decodeRepo =
    map5 Repo
        (field "name" string)
        (field "description" (maybe string))
        (field "html_url" string)
        (field "pushed_at" string)
        (field "stargazers_count" int)
    -- Debug.todo "Implement Model.Repo.decodeRepo"

decodeReposList : Decoder (List Repo)
decodeReposList =
    De.list decodeRepo