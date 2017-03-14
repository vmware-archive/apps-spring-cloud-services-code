module GreetingApp exposing (..)

import Html exposing (Html, h1, text)
import Http exposing (emptyBody)
import Json.Decode as Decode


type alias Flags =
    { apiServerUrl : String }


type RemoteData a
    = Loading
    | Loaded a
    | Failed String


type alias Model =
    { remoteMessage : RemoteData String
    , apiServerUrl : String
    }


type Msg
    = LoadFortune (Result Http.Error String)


init : Flags -> ( Model, Cmd Msg )
init flags =
    ( { apiServerUrl = flags.apiServerUrl
      , remoteMessage = Loading
      }
    , fetchFortune flags.apiServerUrl
    )


fetchFortune : String -> Cmd Msg
fetchFortune apiServerUrl =
    Http.send LoadFortune (Http.getString apiServerUrl)


view : Model -> Html Msg
view model =
    case model.remoteMessage of
        Loading ->
            h1 [] [ text "Loading..." ]

        Loaded msg ->
            h1 [] [ text msg ]

        Failed err ->
            h1 [] [ text ("Failed loading: " ++ err) ]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        LoadFortune result ->
            case result of
                Ok fortune ->
                    ( { model | remoteMessage = Loaded fortune }, Cmd.none )

                Err _ ->
                    ( { model | remoteMessage = Failed "some error occured" }, Cmd.none )


main : Program Flags Model Msg
main =
    Html.programWithFlags
        { init = init
        , view = view
        , update = update
        , subscriptions = \_ -> Sub.none
        }
