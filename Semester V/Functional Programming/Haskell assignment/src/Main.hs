module Main where

import Args
  ( AddOptions (..),
    Args (..),
    GetOptions (..),
    SearchOptions (..),
    parseArgs,
  )
import qualified Data.List as L
import qualified Entry.DB as DB
import Entry.Entry
  ( Entry (..),
    FmtEntry (FmtEntry),
    matchedByAllQueries,
    matchedByQuery,
  )
import Result
import System.Environment (getArgs)
import Test.SimpleTest.Mock
import Prelude hiding (print, putStrLn, readFile)
import qualified Prelude

usageMsg :: String
usageMsg =
  L.intercalate
    "\n"
    [ "snip - code snippet manager",
      "Usage: ",
      "snip add <filename> lang [description] [..tags]",
      "snip search [code:term] [desc:term] [tag:term] [lang:term]",
      "snip get <id>",
      "snip init"
    ]

-- | Handle the init command
handleInit :: TestableMonadIO m => m ()
handleInit = do
  DB.save DB.empty
  return()

-- | Handle the get command
handleGet :: TestableMonadIO m => GetOptions -> m ()
handleGet (GetOptions getEntryId) = do
  dbResult <- DB.load
  case dbResult of
    Error DB.FileNotFound -> putStrLn "Failed to load DB"
    Error DB.CorruptedFile -> putStrLn "Failed to load DB"
    Error DB.InvalidStructure -> putStrLn "Failed to load DB"
    Success db -> case DB.findFirst (\entry -> entryId entry == getEntryId) db of
      Nothing -> putStrLn $ "No entry found with ID: " ++ show getEntryId
      Just entry -> putStrLn (entrySnippet entry)



-- | Handle the search command
handleSearch :: TestableMonadIO m => SearchOptions -> m ()
handleSearch searchOpts = do
  result <- DB.load
  case result of
    Error err -> putStrLn "Failed to load DB"
    Success db -> do
      let queries = searchOptTerms searchOpts  
      let matchedEntries = DB.findAll (\entry -> matchedByAllQueries queries entry) db 
      case matchedEntries of
        [] -> putStrLn "No entries found"
        _  -> mapM_ (putStrLn . show . FmtEntry) matchedEntries


-- | Handle the add command
handleAdd :: TestableMonadIO m => AddOptions -> m ()
handleAdd options = do
    loadedDatabase <- DB.load
    newSnippet <- readFile (addOptFilename options)
    case loadedDatabase of
        Error loadError -> do
            putStrLn "Failed to load Database"
            return ()
        Success existingEntries ->
            case existingEntry of
                Nothing -> do
                    DB.save updatedDatabase
                    return ()
                    
                Just existing -> do
                    putStrLn "Entry with this content already exists: "
                    putStrLn (show (FmtEntry existing))
                    return ()
            where
                initialDatabase = getSuccess loadedDatabase DB.empty
                updatedDatabase = DB.insertWith (\entryId -> createEntry entryId newSnippet options) initialDatabase
                existingEntry = DB.findFirst (\entry -> entrySnippet entry == newSnippet) initialDatabase
  where
    createEntry :: Int -> String -> AddOptions -> Entry
    createEntry entryId snippet opts =
      Entry
        { entryId = entryId,
          entrySnippet = snippet,
          entryFilename = addOptFilename opts,
          entryLanguage = addOptLanguage opts,
          entryDescription = addOptDescription opts,
          entryTags = addOptTags opts
        }



-- | Dispatch the handler for each command
run :: TestableMonadIO m => Args -> m ()
run (Add addOpts) = handleAdd addOpts
run (Search searchOpts) = handleSearch searchOpts
run (Get getOpts) = handleGet getOpts
run Init = handleInit
run Help = putStrLn usageMsg

main :: IO ()
main = do
  args <- getArgs
  let parsed = parseArgs args
  case parsed of
    (Error err) -> Prelude.putStrLn usageMsg
    (Success args) -> run args
