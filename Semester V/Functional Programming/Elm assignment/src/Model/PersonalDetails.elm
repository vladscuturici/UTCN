module Model.PersonalDetails exposing (..)

import Html exposing (..)
import Html.Attributes exposing (class, classList, id)
import Html.Attributes exposing (class, classList, id, href)


type alias DetailWithName =
    { name : String
    , detail : String
    }


type alias PersonalDetails =
    { name : String
    , contacts : List DetailWithName
    , intro : String
    , socials : List DetailWithName
    }


view : PersonalDetails -> Html msg
view details =
    div []
        [ h1 [ id "name" ] [ text details.name ], 
          em [ id "intro" ] [ text details.intro ], 
          div []
              (List.map (\contact ->
                  div [ class "contact-detail" ]
                      [ text (contact.name ++ ": " ++ contact.detail) ]
              ) 
              details.contacts), 
          div []
              (List.map (\social ->
                  a [ class "social-link", href social.detail ]
                    [ text social.name ]
              ) 
              details.socials)
        ]
    -- Debug.todo "Implement the Model.PersonalDetails.view function"
