package Hangman.Control;

import Hangman.Model.HModel;
import Hangman.Model.Status;
import Hangman.View.HView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class represents the Hangman Controller that controls the who game
 */
public class HangmanController implements HController, ActionListener, KeyListener {

  private final HModel model;
  private final HView view;

  /**
   * Constructor for the controller
   *
   * @param model model of the game
   * @param view  view of the game
   */
  public HangmanController(HModel model, HView view) {
    this.model = model;
    this.view = view;
  }


  @Override
  public void play() {
    view.makeVisible();
    view.setGuessActionListener(this);
    view.setEnterKeyListener(this);
    view.appendGameLog("Welcome to Hangman!");
    appendRemainingGuesses();
    updateDisplay(true);
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    handleGuess();
  }

  /**
   * Handles the user's guess, updates the model and the view respectively
   */
  private void handleGuess() {
    String guess = view.getGuess();
    if (model.gameStatus() == Status.Playing) {
      String guessLog = String.format("Your guess: <span style='color:#1400FF'> %s </span>",
          guess.toUpperCase());
      view.appendGameLog(guessLog);
      Boolean isCorrect = null;
      try {
        isCorrect = model.makeGuess(guess);
      } catch (Exception ex) {
        String message = ex.getMessage();
        view.appendGameLog("Whoops!");
        view.appendGameLog(message);
      }
      if (isCorrect != null) {
        String correctness = isCorrect ? "correct" : "incorrect";
        view.appendGameLog("Your guess is " + correctness + ".\n");
      }
      appendRemainingGuesses();
      updateDisplay(Boolean.TRUE.equals(isCorrect));
      appendGameOverLog();
    } else {
      view.appendGameLog("Game is over.");
      view.appendGameLog("Please quit and restart.");
      view.appendGameLog("<br/>");
    }
  }

  /**
   * Appends the remaining guesses message to the view
   */
  private void appendRemainingGuesses() {
    int remainingGuesses = model.getRemainingGuesses();
    String message = String.format("You have <span style='color:#1400FF'>%d</span> guesses left.",
        remainingGuesses);
    view.appendGameLog(message);
    view.appendGameLog("<br/>");
  }

  /**
   * Updates the view displays for word, the graphics, and also the missed characters
   */
  private void updateDisplay(boolean isCorrect) {
    if (isCorrect) {
      boolean[] wordState = model.getWordState();
      String secretWord = model.getSecretWord();
      StringBuilder displayWord = new StringBuilder();

      for (int i = 0; i < wordState.length; i++) {
        if (wordState[i]) {
          displayWord.append(Character.toUpperCase(secretWord.charAt(i))).append(" ");
        } else {
          displayWord.append(" ").append("_").append(" ");
        }
      }
      view.updateWordDisplay(displayWord.toString());
    } else {
      view.updateCanvas(8 - model.getRemainingGuesses());
      view.updateMissedDisplay(model.getWrongGuesses());
    }
  }

  /**
   * Appends the game over log when the game is over to the view
   */
  private void appendGameOverLog() {
    if (model.gameStatus() != Status.Playing) {
      view.appendGameLog("Game is over.");
    }
    if (model.gameStatus() == Status.Lose) {
      view.appendGameLog("You lose.");
      view.appendGameLog("Your man is hanged!");
      String displayMsg = String.format(
          "The secret word is: <span style='color:#1400FF'> %s </span>",
          model.getSecretWord().toUpperCase());
      view.appendGameLog(displayMsg);
      view.appendGameLog("<br/>");
    }
    if (model.gameStatus() == Status.Won) {
      view.appendGameLog("You won!");
      view.appendGameLog("<br/>");
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      handleGuess();
    }
  }
}
