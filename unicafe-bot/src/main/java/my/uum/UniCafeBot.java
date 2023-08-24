package my.uum;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import java.net.URL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniCafeBot extends TelegramLongPollingBot {
    private static Connection connection;
    private FoodManager foodManager;
    private CafeManager cafeManager;
    private CommentManager commentManager;
    private RatingManager ratingManager;
    private CafeAdminManager cafeAdminManager;
    private SystemAdminManager systemAdminManager;
    private Map<Long, UserData> userDataMap;
    private Map<Long, State> stateMap;
    private String currentState;

    public UniCafeBot() {
        stateMap = new HashMap<>();
        userDataMap = new HashMap<>();
        DatabaseConnection dbConnection = new DatabaseConnection();
        connection = dbConnection.getConnection();
        foodManager = new FoodManager(connection);
        cafeManager = new CafeManager(connection);
        cafeAdminManager = new CafeAdminManager(connection);
        systemAdminManager = new SystemAdminManager(connection);
        commentManager = new CommentManager(connection);
        ratingManager = new RatingManager(connection);
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasPhoto()) {
                long chatId = message.getChatId();
                List<PhotoSize> photos = message.getPhoto();
                handleImageMessage(chatId, photos);
            }
            if (message.hasText()) {
                String messageText = message.getText();
                long chatId = message.getChatId();
                State currentState = stateMap.getOrDefault(chatId, new State(StateType.REGISTER));
                switch (messageText.toLowerCase()) {
                    case "/start":
                        sendWelcomeMessage(chatId);
                        break;
                    case "/exit":
                        handleExit(chatId);
                        break;

                    default:
                        switch (currentState.getCurrentState()) {
                            case REGISTER:
                                handleTextMessageRegister(chatId, messageText);
                                break;
                            case LOGIN:
                                handleTextMessageLogin(chatId, messageText);
                                break;
                            case INSERT_CAFE:
                                handleTextMessageInsert(chatId, messageText);
                                break;
                            case INSERT_FOOD:
                                handleTextMessageInsertFood(chatId, messageText);
                                break;
                            case INSERT_IMAGE:
                                handleTextMessageInsertFoodImage(chatId, messageText);
                                break;
                            case LOGIN_SYS:
                                handleTextMessageLoginSys(chatId, messageText);
                                break;
                            case UPDATE_CAFESTATUS:
                                handleTextMessageUpdateStatus(chatId, messageText);
                                break;
                            case STATEDELETEFOOD:
                                handleTextMessageDeleteFood(chatId, messageText);
                                break;
                            case CAFEAPPROVAL:
                                handleTextMessageApproval(chatId, messageText);
                                break;
                            case STATEUPDATECAFE:
                                handleTextMessageCafeUpdate(chatId, messageText);
                                break;
                            case STATEDELETECAFE:
                                handleTextMessageDeleteCafe(chatId, messageText);
                                break;
                            case STATEINSERTRATING:
                                handleTextMessageFoodRating(chatId, messageText);
                                break;
                            case STATEINSERTCOMMENT:
                                handleTextMessageSendComment(chatId, messageText);
                                break;
                            case STATESEARCHFOOD:
                                handleTextMessageSearchFoodName(chatId, messageText);
                                break;
                            case STATECOMPAREFOOD:
                                handleTextMessageCompareFood(chatId, messageText);
                                break;
                            default:
                                sendResponse(chatId, "Invalid command. Please try again.");
                                break;
                        }
                        break;
                }    
            }  
        }
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            State currentStateMain = stateMap.getOrDefault(chatId, new State(StateType.REGISTER));
            State currentStateMainLogin = stateMap.getOrDefault(chatId, new State(StateType.LOGIN));
            State currentStateMainLoginSystem = stateMap.getOrDefault(chatId, new State(StateType.LOGIN_SYS));
            State currentStateMainInsertFood = stateMap.getOrDefault(chatId, new State(StateType.INSERT_FOOD));
            State currentStateMainUpdateStatus = stateMap.getOrDefault(chatId, new State(StateType.UPDATE_CAFESTATUS));
            State currentStateMainDeleteFood = stateMap.getOrDefault(chatId, new State(StateType.STATEDELETEFOOD));
            State currentStateMainCafeApproval = stateMap.getOrDefault(chatId, new State(StateType.CAFEAPPROVAL));
            State currentStateMainUpdateCafe = stateMap.getOrDefault(chatId, new State(StateType.STATEUPDATECAFE));
            State currentStateMainDelete = stateMap.getOrDefault(chatId, new State(StateType.STATEDELETECAFE));
            State currentStateMainInsertRating = stateMap.getOrDefault(chatId, new State(StateType.STATEINSERTRATING));
            State currentStateMainInsertComment = stateMap.getOrDefault(chatId, new State(StateType.STATEINSERTCOMMENT));
            State currentStateMainSearchFood = stateMap.getOrDefault(chatId, new State(StateType.STATESEARCHFOOD));
            State currentStateMainCompareFood = stateMap.getOrDefault(chatId, new State(StateType.STATECOMPAREFOOD));

            if (callbackData.equals("backmenu")) {
            sendWelcomeMessage(chatId);
            } else if (callbackData.equals("guidemenu")) {
            sendGuideCommand(chatId);
            } else if (callbackData.equals("backadmin")) {
                currentState = "welcomeadmin";
            } else if (callbackData.equals("backcafefood")) {
                currentState = "welcomeuser";

            } else if (callbackData.equals("cafe")) {
                currentState = "cafe";
            } else if (callbackData.equals("othercafe")) {
                currentState = "othercafe";
            } else if (callbackData.startsWith("selectedcafe:")) {
                if (callbackData.startsWith("selectedcafe:")) {
                    int index = Integer.parseInt(callbackData.substring("selectedcafe:".length()));
                    String cafeCode = cafeManager.getCafeCodes().get(index);
                    UserData userData = userDataMap.get(chatId);
                    if (userData == null) {
                        userData = new UserData();
                        userDataMap.put(chatId, userData);
                    }
                    userData.setCafeCode(cafeCode);
                    
                    currentState = "selectedcafe:";
                }
            } else if (callbackData.equals("foodmenu")) {
                handleGetFoodItems(chatId);
            } else if (callbackData.equals("givefoodrating")) {
                currentStateMainInsertRating.setCurrentState(StateType.STATEINSERTRATING);
                stateMap.put(chatId, currentStateMainInsertRating); // Update the state in the map
                handleRatingFoodCode(chatId);
            } else if (callbackData.equals("givecafecomment")) {
                currentStateMainInsertComment.setCurrentState(StateType.STATEINSERTCOMMENT);
                stateMap.put(chatId, currentStateMainInsertComment); // Update the state in the map
                handleSendComment(chatId);
            
            } else if (callbackData.equals("food")) {
                currentState = "food";
            } else if (callbackData.equals("comparefood")) {
                currentStateMainCompareFood.setCurrentState(StateType.STATECOMPAREFOOD);
                stateMap.put(chatId, currentStateMainCompareFood); // Update the state in the map
                handleEnterFoodCompare(chatId);
            } else if (callbackData.equals("searchfood")) {
                currentStateMainSearchFood.setCurrentState(StateType.STATESEARCHFOOD);
                stateMap.put(chatId, currentStateMainSearchFood); // Update the state in the map
                handleEnterFoodName(chatId);

            } else if (callbackData.equals("cafeadmin")) {
                currentState = "cafeadmin";
            } else if (callbackData.equals("registercafeadmin")) {
                currentStateMain.setCurrentState(StateType.REGISTER);
                stateMap.put(chatId, currentStateMain); // Update the state in the map
                handleRegister(chatId);
            } else if (callbackData.equals("logincafeadmin")) {
                currentStateMainLogin.setCurrentState(StateType.LOGIN);
                stateMap.put(chatId, currentStateMainLogin); // Update the state in the map
                handleLogin(chatId);
            } else if (callbackData.equals("insertfood")) {
                currentStateMainInsertFood.setCurrentState(StateType.INSERT_FOOD);
                stateMap.put(chatId, currentStateMainInsertFood); // Update the state in the map
                handleInsertFood(chatId);
            } else if (callbackData.equals("updatestatus")) {
                currentStateMainUpdateStatus.setCurrentState(StateType.UPDATE_CAFESTATUS);
                stateMap.put(chatId, currentStateMainUpdateStatus); // Update the state in the map
                handleUpdateStatus(chatId);
            } else if (callbackData.equals("viewfoodmenuadmin")) {
                handleGetFoodMenuAdmin(chatId);
            } else if (callbackData.equals("viewusercomment")) {
                handleGetComments(chatId);
            } else if (callbackData.equals("deletefood")) {
                currentStateMainDeleteFood.setCurrentState(StateType.STATEDELETEFOOD);
                stateMap.put(chatId, currentStateMainDeleteFood); // Update the state in the map
                handleFoodCode(chatId);
            } else if (callbackData.equals("contactadmin")) {
                sendContact(chatId);
                
            } else if (callbackData.equals("sysadmin")) {
                currentState = "sysadmin";
            } else if (callbackData.equals("loginsysadmin")) {
                currentStateMainLoginSystem.setCurrentState(StateType.LOGIN_SYS);
                stateMap.put(chatId, currentStateMainLoginSystem); // Update the state in the map
                handleLoginSys(chatId);
            } else if (callbackData.equals("approvecafe")) {
                currentStateMainCafeApproval.setCurrentState(StateType.CAFEAPPROVAL);
                stateMap.put(chatId, currentStateMainCafeApproval); // Update the state in the map
                handleCafeCodeApproval(chatId);
            } else if (callbackData.equals("updateprofile")) {
                currentStateMainUpdateCafe.setCurrentState(StateType.STATEUPDATECAFE);
                stateMap.put(chatId, currentStateMainUpdateCafe); // Update the state in the map
                handleSendUpdateCafe(chatId);
            } else if (callbackData.equals("deletecafe")) {
                currentStateMainDelete.setCurrentState(StateType.STATEDELETECAFE);
                stateMap.put(chatId, currentStateMainDelete); // Update the state in the map
                handleCafeCodeDelete(chatId);
            } else if (callbackData.equals("viewncafe")) {
                handleViewCafe(chatId);
            } else if (callbackData.equals("viewacafe")) {
                handleViewApproval(chatId);

            } else if (callbackData.equals("exit")) {
                handleExit(chatId);
            }


            /* sendResponseInlineChat(update, createResponseMessage(), createKeyboardMarkup(chatId)); */
            String responseMessage = createResponseMessage();
            InlineKeyboardMarkup keyboardMarkup = createKeyboardMarkup(chatId);
            if (!responseMessage.equals(update.getCallbackQuery().getMessage().getText()) || !keyboardMarkup.equals(update.getCallbackQuery().getMessage().getReplyMarkup())) {
                sendResponseInlineChat(update, responseMessage, keyboardMarkup);
            }
        
        } else if (update.getMessage() != null && update.getMessage().getText().equals("I'm looking for Food and Café")) {
            currentState = "welcomeuser";
            long chatId = update.getMessage().getChatId();
            sendResponseInline(chatId, createResponseMessage(), createKeyboardMarkup(chatId));
        } else if (update.getMessage() != null && update.getMessage().getText().equals("I want to manage cafe")) {
            currentState = "welcomeadmin";
            long chatId = update.getMessage().getChatId();
            sendResponseInline(chatId, createResponseMessage(), createKeyboardMarkup(chatId));
        } else if (update.getMessage() != null && update.getMessage().getText().equals("/user")) {
            currentState = "welcomeuser";
            long chatId = update.getMessage().getChatId();
            sendResponseInline(chatId, createResponseMessage(), createKeyboardMarkup(chatId));
        } else if (update.getMessage() != null && update.getMessage().getText().equals("/cafeadmin")) {
            currentState = "cafeadmin";
            long chatId = update.getMessage().getChatId();
            sendResponseInline(chatId, createResponseMessage(), createKeyboardMarkup(chatId));
        } else if (update.getMessage() != null && update.getMessage().getText().equals("/sysadmin")) {
            currentState = "sysadmin";
            long chatId = update.getMessage().getChatId();
            sendResponseInline(chatId, createResponseMessage(), createKeyboardMarkup(chatId));
        } else if (update.getMessage() != null && update.getMessage().getText().equals("/cafedashboard")) {
            long chatId = update.getMessage().getChatId();
            UserData userData = userDataMap.get(chatId);
            if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
                currentState = "successlogincafeadmin";
                sendResponseInline(chatId, createResponseMessage(), createKeyboardMarkup(chatId));
            } else {
                sendResponse(chatId, "Please login as a cafe admin to access the cafe dashboard.");
            }        
        } else if (update.getMessage() != null && update.getMessage().getText().equals("/systemdashboard")) {
            long chatId = update.getMessage().getChatId();
            UserData userData = userDataMap.get(chatId);
            if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
                currentState = "successloginsysadmin";
                sendResponseInline(chatId, createResponseMessage(), createKeyboardMarkup(chatId));
            } else {
                sendResponse(chatId, "Please login as a system admin to access the system dashboard.");
            }        
        }
        
    }
    
    private String createResponseMessage() {
        String message;
        if (currentState.equals("welcomeuser")) {
            message = "Welcome! Please choose an option:";

        } else if (currentState.equals("cafe")) {
            message = "Inside Cafe";
        } else if (currentState.equals("othercafe")) {
            message = "Inside Cafe";
        } else if (currentState.equals("selectedcafe:")) {
            message = "Hi, Welcome";    

        } else if (currentState.equals("food")) {
            message = "Inside Food";

        } else if (currentState.equals("welcomeadmin")) {
            message = "Hi Admin, Please choose an option:";

        } else if (currentState.equals("cafeadmin")) {
            message = "Hi Cafe Admin, Please choose an option:";
        } else if (currentState.equals("successlogincafeadmin")) {
            message = "Hi Cafe Admin, Please choose an option:";

        } else if (currentState.equals("sysadmin")) {
            message = "Hi System Admin:";
        } else if (currentState.equals("successloginsysadmin")) {
            message = "Hi System Admin, Please choose an option:";

        } else {
            message = "Invalid state";
        }
        return message;
    }
    
    private InlineKeyboardMarkup createKeyboardMarkup(long chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    
        if (currentState.equals("welcomeuser")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton cafeButton = new InlineKeyboardButton("Cafe");
            cafeButton.setCallbackData("cafe");
            row1.add(cafeButton);
    
            InlineKeyboardButton foodButton = new InlineKeyboardButton("Food");
            foodButton.setCallbackData("food");
            row1.add(foodButton);
            keyboard.add(row1);
    
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton backButton = new InlineKeyboardButton("<< Back");
            backButton.setCallbackData("backmenu");
            row2.add(backButton);

            InlineKeyboardButton guideButton = new InlineKeyboardButton(" Guide >>");
            guideButton.setCallbackData("guidemenu");
            row2.add(guideButton);
            keyboard.add(row2);
        } else if (currentState.equals("cafe") || currentState.equals("othercafe")) {
            int cafesPerPage = 6;
            int currentPage = 1;
            int startIndex;
            int endIndex;
            if (currentState.equals("cafe")) {
                startIndex = (currentPage - 1) * cafesPerPage;
                endIndex = Math.min(startIndex + cafesPerPage, cafeManager.getCafeNames().size());
            } else { 
                startIndex = (currentPage - 1) * cafesPerPage + cafesPerPage;
                endIndex = Math.min(startIndex + cafesPerPage, cafeManager.getCafeNames().size());
            }

            List<String> currentPageCafes = cafeManager.getCafeNames().subList(startIndex, endIndex);

            for (int rowIndex = 0; rowIndex < currentPageCafes.size(); rowIndex++) {
                String cafeName = currentPageCafes.get(rowIndex);
                InlineKeyboardButton cafeButton = new InlineKeyboardButton(cafeName);
                String cafeCallbackData = "selectedcafe:"+rowIndex; // Set the callback data with the index
                cafeButton.setCallbackData(cafeCallbackData); // Set the callback data for the button
                List<InlineKeyboardButton> row = Collections.singletonList(cafeButton);
                keyboard.add(row);
            }
            
            if (currentState.equals("cafe")) {
                List<InlineKeyboardButton> row7 = new ArrayList<>();
                InlineKeyboardButton otherCafeButton = new InlineKeyboardButton("Other Cafe");
                otherCafeButton.setCallbackData("othercafe");
                row7.add(otherCafeButton);
                keyboard.add(row7);
            }

            List<InlineKeyboardButton> rowLast = new ArrayList<>();
            InlineKeyboardButton backButton = new InlineKeyboardButton("<< Back To Menu");
            backButton.setCallbackData("backcafefood");
            rowLast.add(backButton);
            keyboard.add(rowLast);
        } else if (currentState.equals("selectedcafe:")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton foodMenuButton = new InlineKeyboardButton("Food Menu");
            foodMenuButton.setCallbackData("foodmenu");
            row1.add(foodMenuButton);
            keyboard.add(row1);

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton foodRatingButton = new InlineKeyboardButton("Give Food Rating");
            foodRatingButton.setCallbackData("givefoodrating");
            row2.add(foodRatingButton);
            InlineKeyboardButton cafeCommentButton = new InlineKeyboardButton("Give Cafe Comment");
            cafeCommentButton.setCallbackData("givecafecomment");
            row2.add(cafeCommentButton);
            keyboard.add(row2);

            List<InlineKeyboardButton> row3 = new ArrayList<>();
            InlineKeyboardButton backcafeButton = new InlineKeyboardButton("<< Back");
            backcafeButton.setCallbackData("backcafe");
            row3.add(backcafeButton);
            keyboard.add(row3);

        } else if (currentState.equals("food")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton listfoodButton = new InlineKeyboardButton("Compare Food by Price & Rating");
            listfoodButton.setCallbackData("comparefood");
            row1.add(listfoodButton);
            keyboard.add(row1);

            List<InlineKeyboardButton> row3 = new ArrayList<>();
            InlineKeyboardButton searchfood = new InlineKeyboardButton("Search Food");
            searchfood.setCallbackData("searchfood");
            row3.add(searchfood);
            keyboard.add(row3);
        
            List<InlineKeyboardButton> row4 = new ArrayList<>();
            InlineKeyboardButton backfoodButton = new InlineKeyboardButton("<< Back");
            backfoodButton.setCallbackData("backcafefood");
            row4.add(backfoodButton);
            keyboard.add(row4);
            
        } else if (currentState.equals("welcomeadmin")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton cafeAdminButton = new InlineKeyboardButton("Cafe Admin");
            cafeAdminButton.setCallbackData("cafeadmin");
            row1.add(cafeAdminButton);
        
            InlineKeyboardButton sysAdminButton = new InlineKeyboardButton("System Admin");
            sysAdminButton.setCallbackData("sysadmin");
            row1.add(sysAdminButton);
            keyboard.add(row1);
        
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton backButton = new InlineKeyboardButton("<< Back");
            backButton.setCallbackData("backmenu");
            row2.add(backButton);
        
            InlineKeyboardButton guideButton = new InlineKeyboardButton("Guide >>");
            guideButton.setCallbackData("guidemenu");
            row2.add(guideButton);
        
            keyboard.add(row2);
        } else if (currentState.equals("cafeadmin")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton registerButton = new InlineKeyboardButton("Register");
            registerButton.setCallbackData("registercafeadmin");
            row1.add(registerButton);
        
            InlineKeyboardButton loginButton = new InlineKeyboardButton("Login");
            loginButton.setCallbackData("logincafeadmin");
            row1.add(loginButton);
            keyboard.add(row1);
        
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton backButton = new InlineKeyboardButton("<< Back");
            backButton.setCallbackData("backadmin");
            row2.add(backButton);
        
            keyboard.add(row2);        
        } else if (currentState.equals("successlogincafeadmin")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton insertFoodButton = new InlineKeyboardButton("Insert Food");
            insertFoodButton.setCallbackData("insertfood");
            row1.add(insertFoodButton);
            keyboard.add(row1);
    
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton updateCafeStatusButton = new InlineKeyboardButton("Update Cafe Status");
            updateCafeStatusButton.setCallbackData("updatestatus");
            row2.add(updateCafeStatusButton);
            keyboard.add(row2);
    
            List<InlineKeyboardButton> row3 = new ArrayList<>();
            InlineKeyboardButton viewFoodMenuButton = new InlineKeyboardButton("View Food Menu");
            viewFoodMenuButton.setCallbackData("viewfoodmenuadmin");
            row3.add(viewFoodMenuButton);
            InlineKeyboardButton viewUserCommentButton = new InlineKeyboardButton("View User Comment");
            viewUserCommentButton.setCallbackData("viewusercomment");
            row3.add(viewUserCommentButton);
            keyboard.add(row3);
    
            List<InlineKeyboardButton> row4 = new ArrayList<>();
            InlineKeyboardButton deleteFoodButton = new InlineKeyboardButton("Delete Food");
            deleteFoodButton.setCallbackData("deletefood");
            row4.add(deleteFoodButton);
            keyboard.add(row4);
    
            List<InlineKeyboardButton> row5 = new ArrayList<>();
            InlineKeyboardButton contactAdminButton = new InlineKeyboardButton("Contact System Admin");
            contactAdminButton.setCallbackData("contactadmin");
            row5.add(contactAdminButton);
            keyboard.add(row5);
    
            List<InlineKeyboardButton> row6 = new ArrayList<>();
            InlineKeyboardButton exitButton = new InlineKeyboardButton("Exit");
            exitButton.setCallbackData("exit");
            row6.add(exitButton);
            keyboard.add(row6);
        } else if (currentState.equals("sysadmin")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton loginSysAdminButton = new InlineKeyboardButton("Login");
            loginSysAdminButton.setCallbackData("loginsysadmin");
            row1.add(loginSysAdminButton);
            keyboard.add(row1);
    
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton backButton = new InlineKeyboardButton("<< Back");
            backButton.setCallbackData("backadmin");
            row2.add(backButton);
            keyboard.add(row2);
        } else if (currentState.equals("successloginsysadmin")) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton approveCafeButton = new InlineKeyboardButton("Approve New Cafe");
            approveCafeButton.setCallbackData("approvecafe");
            row1.add(approveCafeButton);
            keyboard.add(row1);

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton updateCafeProfileButton = new InlineKeyboardButton("Update Cafe Profile");
            updateCafeProfileButton.setCallbackData("updateprofile");
            row2.add(updateCafeProfileButton);
            keyboard.add(row2);

            List<InlineKeyboardButton> row3 = new ArrayList<>();
            InlineKeyboardButton deleteCafeButton = new InlineKeyboardButton("Delete Cafe");
            deleteCafeButton.setCallbackData("deletecafe");
            row3.add(deleteCafeButton);
            keyboard.add(row3);

            List<InlineKeyboardButton> row4 = new ArrayList<>();
            InlineKeyboardButton viewCafeNonApprovedButton = new InlineKeyboardButton("View Non Approved Cafe");
            viewCafeNonApprovedButton.setCallbackData("viewncafe");
            row4.add(viewCafeNonApprovedButton);
            InlineKeyboardButton viewCafeApprovedButton = new InlineKeyboardButton("View Approved Cafe");
            viewCafeApprovedButton.setCallbackData("viewacafe");
            row4.add(viewCafeApprovedButton);
            keyboard.add(row4);

            List<InlineKeyboardButton> row5 = new ArrayList<>();
            InlineKeyboardButton exitButton = new InlineKeyboardButton("Exit");
            exitButton.setCallbackData("exit");
            row5.add(exitButton);
            keyboard.add(row5);
        }

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }        
        
    private void handleExit(long chatId) {
        userDataMap.remove(chatId);
        sendResponse(chatId, "User data and operation has been reset. Use /start to begin.");
    }
        
    private void handleTextMessageRegister(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            RegistrationStep registrationStep = userData.getRegistrationStep();
            switch (registrationStep) {
                case EMAIL:
                    handleEmailInputForRegister(chatId, messageText);
                    break;
                case NAME:
                    handleNameInputForRegister(chatId, messageText);
                    break;
                case PASSWORD:
                    handlePasswordInputForRegister(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void handleRegister(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            sendResponse(chatId, "You are already registered as a cafe admin.");
        } else {
            if (userData == null) {
                userData = new UserData();
                userDataMap.put(chatId, userData);
            }
            sendResponse(chatId, "Please enter your email address to register:");
            userData.setRegistrationStep(RegistrationStep.EMAIL);
            userData.setUserType(UserType.CAFE_ADMIN);
        }
    }
    
    private void handleEmailInputForRegister(long chatId, String emailAddress) {
        UserData userData = userDataMap.get(chatId);
        userData.setEmailAddress(emailAddress);
        sendResponse(chatId, "Please enter your name:");
        userData.setRegistrationStep(RegistrationStep.NAME);
    }
    
    private void handleNameInputForRegister(long chatId, String name) {
        UserData userData = userDataMap.get(chatId);
        userData.setName(name);
        sendResponse(chatId, "Please enter your desired password:");
        userData.setRegistrationStep(RegistrationStep.PASSWORD);
    }
    
    private void handlePasswordInputForRegister(long chatId, String password) {
        State currentState = stateMap.getOrDefault(chatId, new State(StateType.INSERT_CAFE));
        UserData userData = userDataMap.get(chatId);
        userData.setPassword(password);
        boolean registrationSuccess = cafeAdminManager.registerCafeAdmin(
                userData.getEmailAddress(),
                userData.getName(),
                userData.getPassword()
        );
        if (registrationSuccess) {
            sendResponse(chatId, "Registration successful. You are now registered as a cafe admin.");
            currentState.setCurrentState(StateType.INSERT_CAFE);
            stateMap.put(chatId, currentState); // Update the state in the map
            handleInsert(chatId);
        } else {
            sendResponse(chatId, "Registration failed. Please try again.");
        }
    }
    
    private void handleTextMessageInsert(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            InsertStep insertStep = userData.getInsertStep();
            switch (insertStep) {
                case INSERTFORM_CAFE:
                    handleInsertCafe(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }
    private void handleInsert(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            sendResponse(chatId, "Please login as a system admin to perform this action.");
            return;
        }
        sendResponse(chatId, "Please enter the details of the cafe in the following format:\n");
        sendResponse(chatId,
                "Cafe Code: cafeCode\n" +
                "Name: CafeName\n" +
                "Inasis Name: inasisName\n" +
                "Office TelNo: officeTelNo\n" +
                "Mobile TelNo: mobileTelNo\n" +
                "Location: locationAddress\n" +
                "Location Link: locationLink\n" +
                "Open Time: openTime\n" +
                "Close Time: closeTime\n" +
                "Status: Status\n" +
                "Description: description\n");
        userData.setInsertStep(InsertStep.INSERTFORM_CAFE);
    }
    
    private void handleInsertCafe(long chatId, String messageText) {
        String[] lines = messageText.split("\n");
        String cafeCode = extractValue(lines[0]);
        String name = extractValue(lines[1]);
        String inasisName = extractValue(lines[2]);
        String officeTelNo = extractValue(lines[3]);
        String mobileTelNo = extractValue(lines[4]);
        String location = extractValue(lines[5]);
        String locationLink = extractValue(lines[6]);
        String openTime = extractValue(lines[7]);
        String closeTime = extractValue(lines[8]);
        String holidayStatus = extractValue(lines[9]);
        String description = extractValue(lines[10]);
        UserData userData = userDataMap.get(chatId);

        Cafe cafe = new Cafe(cafeCode, name, inasisName, officeTelNo, mobileTelNo, location, locationLink, openTime, closeTime, holidayStatus, description, userData.getEmailAddress());
        
        boolean success = cafeManager.insertCafe(cafe);
        
        if (success) {
            sendResponse(chatId, "Cafe inserted successfully. Now you can login at /cafeadmin");
            stateMap.remove(chatId);
            userDataMap.remove(chatId);
        } else {
            sendResponse(chatId, "Failed to insert the cafe.");
            stateMap.remove(chatId);
            userDataMap.remove(chatId);
        }
    }
    
    private String extractValue(String line) {
        return line.substring(line.indexOf(":") + 1).trim();
    }
    
    private void handleTextMessageInsertFood(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            InsertFoodStep insertFoodStep = userData.getInsertFoodStep();
            switch (insertFoodStep) {
                case INSERTFOOD_CAFE:
                    handleInsertFoodDetails(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleInsertFood(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            sendResponse(chatId, "Please login as a cafe admin to perform this action.");
            return;
        }
        sendResponse(chatId, "Please enter the details of the food in the following format:\n");
        sendResponse(chatId,
        "Food Code: foodCode\n" +
        "Cafe Code: cafeCode\n" +
        "Food Type: foodType\n" +
        "Food Name: foodName\n" +
        "Food Price: 100\n" +
        "Status: status");
        userData.setInsertFoodStep(InsertFoodStep.INSERTFOOD_CAFE);
    }

    private void handleInsertFoodDetails(long chatId, String messageText) {
        State currentState = stateMap.getOrDefault(chatId, new State(StateType.INSERT_CAFE));
        UserData userData = userDataMap.get(chatId);
        String[] lines = messageText.split("\n");
        String foodCode = extractValue(lines[0]);
        String cafeCoded = extractValue(lines[1]);
        String foodType = extractValue(lines[2]);
        String foodName = extractValue(lines[3]);
        double foodPrice = Double.parseDouble(extractValue(lines[4]));
        String status = extractValue(lines[5]);
    
        Food food = new Food(foodCode, cafeCoded, foodType, foodName, foodPrice, null, status);
        userData.setFood(food);
        
        boolean success = foodManager.insertFood(food);
        if (success) {
            sendResponse(chatId, "Food details inserted successfully.");
            currentState.setCurrentState(StateType.INSERT_IMAGE);
            stateMap.put(chatId, currentState); // Update the state in the map
            handleInsertFoodImage(chatId);
        } else {
            sendResponse(chatId, "Failed to insert the food details.");
        }
    }

    private void handleTextMessageInsertFoodImage(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            InsertFoodStep insertFoodStep = userData.getInsertFoodStep();
            switch (insertFoodStep) {
                case INSERTFOOD_IMG:
                    List<PhotoSize> photos = new ArrayList<>();
                    PhotoSize photo = new PhotoSize();
                    photo.setFileId(messageText);
                    photos.add(photo);
                    handleImageMessage(chatId, photos);
                    break; 
                default:
                    break;
            }
        }
    }
    
    private void handleInsertFoodImage(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            sendResponse(chatId, "Please login as a system admin to perform this action.");
            return;
        }
        sendResponse(chatId,"Please submit the food image now.");
        userData.setInsertFoodStep(InsertFoodStep.INSERTFOOD_IMG);
    }
    private void handleImageMessage(long chatId, List<PhotoSize> photos) {
        Food food = userDataMap.get(chatId).getFood();
        if (food != null) {
            try {
                PhotoSize photo = photos.get(photos.size() - 1);
                String fileId = photo.getFileId();
    
                GetFile getFileMethod = new GetFile();
                getFileMethod.setFileId(fileId);
    
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
    
                byte[] imageBytes = downloadFileAsBytes(file);
    
                food.setFoodImage(imageBytes);
                foodManager.updateFood(food);
    
                sendResponse(chatId, "Food image saved successfully.");
            } catch (TelegramApiException | IOException e) {
                sendResponse(chatId, "Failed to save the food image.");
            }
        } else {
            sendResponse(chatId, "No food details found. Please enter the food details first.");
        }
    }
    
    private byte[] downloadFileAsBytes(org.telegram.telegrambots.meta.api.objects.File file) throws TelegramApiException, IOException {
        if (file != null) {
            String filePath = file.getFilePath();
            if (filePath != null) {
                String fileUri = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
                URL url = new URL(fileUri);
                try (InputStream in = url.openStream()) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    return out.toByteArray();
                }
            }
        }
        throw new TelegramApiException("File is null or file path is not available.");
    }
    
    private void handleTextMessageLogin(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            LoginStep loginStep = userData.getLoginStep();
            switch (loginStep) {
                case EMAIL:
                    handleEmailInputForLogin(chatId, messageText);
                    break;
                case PASSWORD:
                    handlePasswordInputForLogin(chatId, messageText);
                    break;
                default:  
            }
        }
    }

    private void handleLogin(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            sendResponse(chatId, "You are already logged as a cafe admin.");
        } else {
            if (userData == null) {
                userData = new UserData();
                userDataMap.put(chatId, userData);
            }
            sendResponse(chatId, "Please enter your email address:");
            userData.setLoginStep(LoginStep.EMAIL);
            userData.setUserType(UserType.CAFE_ADMIN);
        }
    }

    private void handleEmailInputForLogin(long chatId, String emailAddress) {
        UserData userData = userDataMap.get(chatId);
        userData.setEmailAddress(emailAddress);
        sendResponse(chatId, "Please enter your password:");
        userData.setLoginStep(LoginStep.PASSWORD);
    }

    private void handlePasswordInputForLogin(long chatId, String password) {
        UserData userData = userDataMap.get(chatId);
        String emailAddress = userData.getEmailAddress();
        CafeAdmin cafeAdmin = cafeAdminManager.loginCafeAdmin(emailAddress, password);
        if (cafeAdmin != null) {
            sendResponse(chatId, "Login successful. Welcome, " + cafeAdmin.getName() + "!\nGo to /cafedashboard to manage cafe");
            

        } else {
            sendResponse(chatId, "Login failed. Please try again.");
            
        }
    }

    private void handleTextMessageLoginSys(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
            LoginStep loginStep = userData.getLoginStep();
            switch (loginStep) {
                case EMAIL:
                    handleEmailInputForLoginSys(chatId, messageText);
                    break;
                case PASSWORD:
                    handlePasswordInputForLoginSys(chatId, messageText);
                    break;
                default:  
            }
        }
    }

    private void handleLoginSys(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && (userData.getUserType() == UserType.CAFE_ADMIN || userData.getUserType() == UserType.SYSTEM_ADMIN)) {
            sendResponse(chatId, "You are already logged in as a cafe admin or system admin.");
        } else {
            if (userData == null) {
                userData = new UserData();
                userDataMap.put(chatId, userData);
            }
            sendResponse(chatId, "Please enter your email address:");
            userData.setLoginStep(LoginStep.EMAIL);
            userData.setUserType(UserType.SYSTEM_ADMIN);
        }
    }    

    private void handleEmailInputForLoginSys(long chatId, String email) {
        UserData userData = userDataMap.get(chatId);
        userData.setEmailAddress(email);
        sendResponse(chatId, "Please enter your password:");
        userData.setLoginStep(LoginStep.PASSWORD);
    }

    private void handlePasswordInputForLoginSys(long chatId, String password) {
        UserData userData = userDataMap.get(chatId);
        String email = userData.getEmailAddress();
        SystemAdmin systemAdmin = systemAdminManager.login(email, password);
        if (systemAdmin != null) {
            sendResponse(chatId, "Login successful. Welcome!\nGo to /systemdashboard to manage cafe");
            stateMap.remove(chatId);
        } else {
            sendResponse(chatId, "Login failed. Please try again.");
        }
    }

    private void handleTextMessageUpdateStatus(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            UpdateStatus updateStatus = userData.getUpdateStatus();
            switch (updateStatus) {
                case UPDATE_STATUS:
                    handleNewUpdateStatus(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void handleUpdateStatus(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            sendResponse(chatId, "Please login as a cafe admin to perform this action.");
            return;
        }
        sendResponse(chatId, "Please enter the new cafe status");
        userData.setUpdateStatus(UpdateStatus.UPDATE_STATUS);
    }

    private void handleNewUpdateStatus(long chatId, String updateStatus) {
        UserData userData = userDataMap.get(chatId);
        String emailAddress = userData.getEmailAddress();
    
        boolean success = cafeManager.updateHolidayStatus(emailAddress, updateStatus);
        if (success) {
            sendResponse(chatId, "Update Status successfully.");
        } else {
            sendResponse(chatId, "Failed to update the status.");
        }
    }

    private void handleGetComments(long chatId) {
        UserData userData = userDataMap.get(chatId);
        String cafeCode = userData.getCafeCode();
        List<Comment> comments = commentManager.getComments(cafeCode);
    
        if (comments.isEmpty()) {
            sendResponse(chatId, "No comments found.");
        } else {
            StringBuilder response = new StringBuilder("Comments:\n");
            for (Comment comment : comments) {
                response.append("Comment: ").append(comment.getComment())
                        .append("\n\n");
            }
            sendResponse(chatId, response.toString());
        }
        stateMap.remove(chatId);
    }
    
    public void handleSendComment(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            userData = new UserData();
            userDataMap.put(chatId, userData);
        }
        sendResponse(chatId, "Please enter the comment:");
        userData.setInsertComment(InsertComment.INSERTCOMMENT);
        userData.setUserType(UserType.USER);
    }
    
    private void handleTextMessageSendComment(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.USER) {
            InsertComment insertComment = userData.getInsertComment();
            switch (insertComment) {
                case INSERTCOMMENT:
                    handleInsertComment(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void handleInsertComment(long chatId, String commentText) {
        UserData userData = userDataMap.get(chatId);
        String cafeCode = userData.getCafeCode();
    
        Comment comment = new Comment();
        comment.setCafeCode(cafeCode);
        comment.setComment(commentText);

        boolean success = commentManager.insertComment(comment);
    
        if (success) {
            sendResponse(chatId, "Comment saved successfully.");
        } else {
            sendResponse(chatId, "Failed to save the comment.");
        }
    }    
            
    private void handleFoodCode(long chatId) {
        UserData userData = userDataMap.get(chatId);
        sendResponse(chatId, "Please enter the food code");
        userData.setDeleteFood(DeleteFood.DELETE_FOOD);
    }
    
    private void handleTextMessageDeleteFood(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.CAFE_ADMIN) {
            DeleteFood deleteFood = userData.getDeleteFood();
            switch (deleteFood) {
                case DELETE_FOOD:
                    handleDeleteFood(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }    
    
    private void handleDeleteFood(long chatId, String foodCode) {
        UserData userData = userDataMap.get(chatId);
        String emailAdmin = userData.getEmailAddress();
        boolean success = foodManager.deleteFood(foodCode, emailAdmin);
        if (success) {
            sendResponse(chatId, "Food deleted successfully.");
        } else {
            sendResponse(chatId, "Failed to delete the food.");
        }
    }
    
    private void handleTextMessageApproval(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
            Approval approval = userData.getApproval();
            switch (approval) {
                case APPROVAL:
                    handleApproval(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleCafeCodeApproval(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
            sendResponse(chatId, "Please enter the cafe code:");
            userData.setApproval(Approval.APPROVAL);
        } else {
            sendResponse(chatId, "Please login as a system admin to perform this action.");
        }
    }    

    private void handleApproval(long chatId, String cafeCode) {
        if (cafeManager.insertCafeApproval(cafeCode)) {
            sendResponse(chatId, "Cafe approval successful.");
        } else {
            sendResponse(chatId, "Failed to approve the cafe.");
        }
    }

    private void handleTextMessageCafeUpdate(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
            CafeUpdate cafeUpdate = userData.getCafeUpdate();
            switch (cafeUpdate) {
                case CAFEUPDATE:
                    handleCafeUpdate(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }
    
    private void handleSendUpdateCafe(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null || userData.getUserType() != UserType.SYSTEM_ADMIN) {
            sendResponse(chatId, "Please login as a system admin to perform this action.");
            return;
        }
        sendResponse(chatId, "Please submit the updated cafe form:\n" +
        "Cafe Code: cafeCode\n" +
        "Name: CafeName\n" +
        "Inasis Name: inasisName\n" +
        "Office TelNo: officeTelNo\n" +
        "Mobile TelNo: mobileTelNo\n" +
        "Location: locationAddress\n" +
        "Location Link: locationLink\n" +
        "Open Time: openTime\n" +
        "Close Time: closeTime\n" +
        "Status: Status\n" +
        "Description: description\n" +
        "emailAdmin: emailAdmin");
        userData.setCafeUpdate(CafeUpdate.CAFEUPDATE);
    }
    
    private void handleCafeUpdate(long chatId, String messageText) {        
        String[] lines = messageText.split("\n");
        String cafeCode = extractValue(lines[0]);
        String name = extractValue(lines[1]);
        String inasisName = extractValue(lines[2]);
        String officeTelNo = extractValue(lines[3]);
        String mobileTelNo = extractValue(lines[4]);
        String location = extractValue(lines[5]);
        String locationLink = extractValue(lines[6]);
        String openTime = extractValue(lines[7]);
        String closeTime = extractValue(lines[8]);
        String holidayStatus = extractValue(lines[9]);
        String description = extractValue(lines[10]);
        String emailAdmin = extractValue(lines[11]);
        
        Cafe cafe = new Cafe(cafeCode, name, inasisName, officeTelNo, mobileTelNo, location, locationLink, openTime, closeTime, holidayStatus, description, emailAdmin);
        
        boolean success = cafeManager.updateCafe(cafe);
        
        if (success) {
            sendResponse(chatId, "Cafe updated successfully.");
        } else {
            sendResponse(chatId, "Failed to update the cafe.");
        }
    }

    private void handleViewCafe(long chatId) {
        List<Cafe> cafes = cafeManager.getViewCafes();
    
        if (cafes.isEmpty()) {
            sendResponse(chatId, "No cafes found.");
        } else {
            StringBuilder response = new StringBuilder("Cafes:\n");
            for (Cafe cafe : cafes) {
                response.append("- Cafe Code: ").append(cafe.getCafeCode())
                        .append("\n  Name: ").append(cafe.getName())
                        .append("\n  Inasis Name: ").append(cafe.getInasisName())
                        .append("\n  Office TelNo: ").append(cafe.getOfficeTelNo())
                        .append("\n  Mobile TelNo: ").append(cafe.getMobileTelNo())
                        .append("\n  Location: ").append(cafe.getLocation())
                        .append("\n  Location Link: ").append(cafe.getLocationLink())
                        .append("\n  Open Time: ").append(cafe.getOpenTime())
                        .append("\n  Close Time: ").append(cafe.getCloseTime())
                        .append("\n  Holiday Status: ").append(cafe.getHolidayStatus())
                        .append("\n  Description: ").append(cafe.getDescription())
                        .append("\n  Email Admin: ").append(cafe.getEmailAdmin())
                        .append("\n\n");
            }
            sendResponse(chatId, response.toString());
            stateMap.remove(chatId);
        }
    }

    private void handleViewApproval(long chatId) {
        List<Cafe> approvedCafes = cafeManager.getViewApproval();
    
        if (approvedCafes.isEmpty()) {
            sendResponse(chatId, "No approved cafes found.");
        } else {
            StringBuilder response = new StringBuilder("Approved Cafes:\n");
            for (Cafe cafe : approvedCafes) {
                response.append("Cafe Code: ").append(cafe.getCafeCode())
                        .append("\nName: ").append(cafe.getName())
                        .append("\nInasis Name: ").append(cafe.getInasisName())
                        .append("\nOffice Tel No: ").append(cafe.getOfficeTelNo())
                        .append("\nMobile Tel No: ").append(cafe.getMobileTelNo())
                        .append("\nLocation: ").append(cafe.getLocation())
                        .append("\nLocation Link: ").append(cafe.getLocationLink())
                        .append("\nOpen Time: ").append(cafe.getOpenTime())
                        .append("\nClose Time: ").append(cafe.getCloseTime())
                        .append("\nHoliday Status: ").append(cafe.getHolidayStatus())
                        .append("\nDescription: ").append(cafe.getDescription())
                        .append("\nEmail Admin: ").append(cafe.getEmailAdmin())
                        .append("\n\n");
            }
            sendResponse(chatId, response.toString());
            stateMap.remove(chatId);
        }
    }    
    
    private void sendResponse(long chatId, String message) {
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText(message);

        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }    

    private void sendResponseInline(long chatId, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText(message);
        response.setReplyMarkup(inlineKeyboardMarkup);
    
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendResponseInlineChat(Update update, String message, InlineKeyboardMarkup keyboardMarkup) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
    
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(message);
        editMessageText.setReplyMarkup(keyboardMarkup);
    
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleTextMessageDeleteCafe(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
            DeleteCafe deleteCafe = userData.getDeleteCafe();
            switch (deleteCafe) {
                case DELETECAFE:
                    handleDeleteCafe(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleCafeCodeDelete(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
            sendResponse(chatId, "Please enter the cafe code:");
            userData.setDeleteCafe(DeleteCafe.DELETECAFE);
        } else {
            sendResponse(chatId, "Please login as a system admin to perform this action.");
        }
    }    

    private void handleDeleteCafe(long chatId, String cafeCode) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.SYSTEM_ADMIN) {
            boolean success = cafeManager.deleteCafe(cafeCode);
            if (success) {
                sendResponse(chatId, "Cafe deleted successfully.");
            } else {
                sendResponse(chatId, "Failed to delete the cafe.");
            }
        } else {
            sendResponse(chatId, "Please login as a system admin to perform this action.");
        }
    }

    public void handleGetFoodItems(long chatId) {
        UserData userData = userDataMap.get(chatId);
        String cafeCode = userData.getCafeCode();
        List<Food> foodItems = foodManager.getFoodItems(cafeCode);

        StringBuilder response = new StringBuilder("List of food menu\n");
        sendResponse(chatId, response.toString());
        for (Food food : foodItems) {
            String foodCode = food.getFoodCode();
            String foodName = food.getFoodName();
            double foodPrice = food.getFoodPrice();
            byte[] foodImage = food.getFoodImage();

            float averageRating = ratingManager.getAverageRating(foodCode);

            String message = "Food Code: " + foodCode + "\nFood Name: " + foodName + "\nPrice: RM" + foodPrice
                    + "\nAverage Rating: " + averageRating;

            InputFile inputFile = new InputFile(new ByteArrayInputStream(foodImage), "food_image.jpg");

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(chatId));
            sendPhoto.setCaption(message);
            sendPhoto.setPhoto(inputFile);

            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        stateMap.remove(chatId);
    }


    public void handleRatingFoodCode(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            userData = new UserData();
            userDataMap.put(chatId, userData);
        }
        sendResponse(chatId, "Please enter the food code:");
        userData.setInsertRating(InsertRating.INSERTRATINGCODE);
        userData.setUserType(UserType.USER);
    }
    
    private void handleTextMessageFoodRating(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.USER) {
            InsertRating insertRating = userData.getInsertRating();
            switch (insertRating) {
                case INSERTRATINGCODE:
                    handleFoodCodeRating(chatId, messageText);
                    break;
                case INSERTRATING:
                    handleFoodRating(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void handleFoodCodeRating(long chatId, String foodCode) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.USER) {
            if (foodManager.isValidFoodCode(foodCode)) {
                userData.setFoodCode(foodCode);
                sendResponse(chatId, "Please rate the food from 1 to 5.");
                userData.setInsertRating(InsertRating.INSERTRATING);
            } else {
                sendResponse(chatId, "Invalid food code. Please enter a valid food code.");
            }
        }
    }
    
    public void handleFoodRating(long chatId, String messageText) {
        try {
            float rating = Float.parseFloat(messageText);
            if (rating >= 1 && rating <= 5) {
                UserData userData = userDataMap.get(chatId);
                if (userData != null && userData.getUserType() == UserType.USER) {
                    String foodCode = userData.getFoodCode();
                    ratingManager.insertRating(foodCode, rating);
                    sendResponse(chatId, "Thank you for rating the food!");
                }
            } else {
                sendResponse(chatId, "Invalid rating. Please rate the food from 1 to 5.");
            }
        } catch (NumberFormatException e) {
            sendResponse(chatId, "Invalid rating. Please rate the food from 1 to 5.");
        }
    }    
    
    private void sendWelcomeMessage(long chatId) {
        SendMessage welcomeMessage = new SendMessage();
        welcomeMessage.setChatId(String.valueOf(chatId));
        welcomeMessage.setText("Welcome to UniCafe-bot! How can I assist you today?");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardRow foodAndCafeRow = new KeyboardRow();
        foodAndCafeRow.add("I'm looking for Food and Café");

        KeyboardRow adminRow = new KeyboardRow();
        adminRow.add("I want to manage cafe");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(foodAndCafeRow);
        keyboard.add(adminRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        welcomeMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(welcomeMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    public void handleEnterFoodName(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            userData = new UserData();
            userDataMap.put(chatId, userData);
        }
        sendResponse(chatId, "Please enter the food name:");
        userData.setSearchFood(SearchFood.SEARCHFOOD);
        userData.setUserType(UserType.USER);
    }

    private void handleTextMessageSearchFoodName(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.USER) {
            SearchFood searchFood = userData.getSearchFood();
            switch (searchFood) {
                case SEARCHFOOD:
                    handleSearchFoodName(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }

    public void handleSearchFoodName(long chatId, String keyword) {
        List<Food> matchingFoods = foodManager.searchFoodByName(keyword);
    
        if (matchingFoods.isEmpty()) {
            sendResponse(chatId, "No matching foods found.");
        } else {
            for (Food food : matchingFoods) {
                String foodCode = food.getFoodCode();
                String foodName = food.getFoodName();
                double foodPrice = food.getFoodPrice();
                byte[] foodImage = food.getFoodImage();
    
                float averageRating = ratingManager.getAverageRating(foodCode);

                String message = "Food Code: " + foodCode + "\nFood Name: " + foodName + "\nPrice: RM" + foodPrice
                + "\nAverage Rating: " + averageRating;

                InputFile inputFile = new InputFile(new ByteArrayInputStream(foodImage), "food_image.jpg");
    
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(String.valueOf(chatId));
                sendPhoto.setCaption(message);
                sendPhoto.setPhoto(inputFile);
    
                // Send the photo with the message
                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void handleEnterFoodCompare(long chatId) {
        UserData userData = userDataMap.get(chatId);
        if (userData == null) {
            userData = new UserData();
            userDataMap.put(chatId, userData);
        }
        sendResponse(chatId, "Please enter the food name that you want to compare:");
        userData.setCompareFood(CompareFood.COMPAREFOOD);
        userData.setUserType(UserType.USER);
    }

    private void handleTextMessageCompareFood(long chatId, String messageText) {
        UserData userData = userDataMap.get(chatId);
        if (userData != null && userData.getUserType() == UserType.USER) {
            CompareFood compareFood = userData.getCompareFood();
            switch (compareFood) {
                case COMPAREFOOD:
                    handleCompareFood(chatId, messageText);
                    break;
                default:
                    break;
            }
        }
    }

    public void handleCompareFood(long chatId, String keyword) {
        List<Food> matchingFoods = foodManager.searchFoodByName(keyword);
        if (matchingFoods.isEmpty()) {
            sendResponse(chatId, "No matching foods found.");
        } else {
            Collections.sort(matchingFoods, new Comparator<Food>() {
                @Override
                public int compare(Food food1, Food food2) {
                    int priceComparison = Double.compare(food1.getFoodPrice(), food2.getFoodPrice());
                    if (priceComparison != 0) {
                        return priceComparison;
                    }
                    float rating1 = ratingManager.getAverageRating(food1.getFoodCode());
                    float rating2 = ratingManager.getAverageRating(food2.getFoodCode());
                    return Float.compare(rating2, rating1);
                }
            });
            for (Food food : matchingFoods) {
                String foodCode = food.getFoodCode();
                String foodName = food.getFoodName();
                double foodPrice = food.getFoodPrice();
                byte[] foodImage = food.getFoodImage();
    
                float averageRating = ratingManager.getAverageRating(foodCode);
    
                String message = "Food Code: " + foodCode + "\nFood Name: " + foodName + "\nPrice: RM" + foodPrice
                        + "\nAverage Rating: " + averageRating;
    
                InputFile inputFile = new InputFile(new ByteArrayInputStream(foodImage), "food_image.jpg");
    
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(String.valueOf(chatId));
                sendPhoto.setCaption(message);
                sendPhoto.setPhoto(inputFile);

                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }    

    public void handleGetFoodMenuAdmin(long chatId) {
        UserData userData = userDataMap.get(chatId);
        String emailAdmin = userData.emailAddress;
        List<Food> foodItems = foodManager.getFoodItemsAdmin(emailAdmin);

        StringBuilder response = new StringBuilder("List of your cafe food menu\n");
        sendResponse(chatId, response.toString());
        for (Food food : foodItems) {
            String foodCode = food.getFoodCode();
            String foodName = food.getFoodName();
            double foodPrice = food.getFoodPrice();
            byte[] foodImage = food.getFoodImage();

            float averageRating = ratingManager.getAverageRating(foodCode);

            String message = "Food Code: " + foodCode + "\nFood Name: " + foodName + "\nPrice: RM" + foodPrice
                    + "\nAverage Rating: " + averageRating;

            InputFile inputFile = new InputFile(new ByteArrayInputStream(foodImage), "food_image.jpg");

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(chatId));
            sendPhoto.setCaption(message);
            sendPhoto.setPhoto(inputFile);

            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        stateMap.remove(chatId);
    }
    
    private void sendGuideCommand(long chatId) {
        SendMessage commandMessage = new SendMessage();
        commandMessage.setChatId(String.valueOf(chatId));
        commandMessage.setText("Hi! Welcome to UNICAFE-BOT. \uD83D\uDE0A\n\nTo use the inline keyboard, you can start by typing <b>/start</b> and selecting your preferred option based on your needs.\n\n"
                + "If you want to search for cafes and food, you can choose the <b>I'm looking for food and cafes</b> option. From there, you can find your preferred option by selecting either <b>Cafe</b> or <b>Food</b> to get the best food searching experience.\n\n"
                + "If you want to manage cafes, you can follow the inline keyboard to the <b>Cafe Admin</b> option. There, you can choose to <b>Register</b> if you are a new admin and follow the registration process, including providing your email and username. After that, you can fill in the cafe data form and you'll be free to add menus, update statuses, upload food images, and enjoy other features.\n\n"
                + "Guide: \uD83D\uDCD6\n"
                + "<b>/start</b> - Main page \uD83C\uDFE0\n"
                + "User can choose to look for cafes and food or manage cafes.\n\n"
                + "<b>/user</b> - Menu for cafes and food \uD83C\uDF73\uD83C\uDF5D\n"
                + "Users can choose their preferred way to search for food, sort it by cafe, and find details about location, menu, rating & comments. They can also find direct preferred food options sorted by price and rating. Users can search for food by name.\n\n"
                + "<b>/cafeadmin</b> - Register as a cafe admin and manage cafes \uD83C\uDFEB\n"
                + "Cafe admins can insert and update cafe profiles, menus, food images, and locations.\n\n"
                + "Please feel free to explore and enjoy UNICAFE-BOT! \uD83C\uDF73\uD83C\uDF5D\uD83C\uDFEB");
    
        commandMessage.setParseMode(ParseMode.HTML);
    
        try {
            execute(commandMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendContact(long chatId) {
        SendMessage commandMessage = new SendMessage();
        commandMessage.setChatId(String.valueOf(chatId));
        commandMessage.setText("Hi! Welcome Cafe Admin.\n"
                + "If you have any inquiries, problems, or updates about cafe data\n"
                + "or your cafe admin account, you can reach the system admin at muhammadrifkiabdillah@gmail.com.\n\n"
                + "Please feel free to explore and enjoy UNICAFE-BOT! \uD83C\uDF73\uD83C\uDF5D\uD83C\uDFEB");
        commandMessage.setParseMode(ParseMode.HTML);
    
        try {
            execute(commandMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }    

    private enum StateType {
    REGISTER,
    LOGIN,
    INSERT_CAFE,
    INSERT_FOOD,
    INSERT_IMAGE,
    LOGIN_SYS,
    UPDATE_CAFESTATUS,
    STATEDELETEFOOD,
    CAFEAPPROVAL,
    STATEUPDATECAFE,
    STATEDELETECAFE,
    STATEINSERTCOMMENT,
    STATEINSERTRATING,
    STATESEARCHFOOD,
    STATECOMPAREFOOD
    }

    private class State {
        private StateType currentState;

        public State(StateType initialState) {
            currentState = initialState;
        }

        public StateType getCurrentState() {
            return currentState;
        }

        public void setCurrentState(StateType newState) {
            currentState = newState;
        }
    }
    
    private class UserData {
        private String emailAddress;
        private String name;
        private String password;
        private String cafeCode;
        private String foodCode;
        private RegistrationStep registrationStep;
        private LoginStep loginStep;
        private InsertStep insertStep;
        private InsertFoodStep insertFoodStep;
        private UserType userType;
        private UpdateStatus updateStatus;
        private DeleteFood deleteFood;
        private Approval approval;
        private CafeUpdate cafeUpdate;
        private DeleteCafe deleteCafe;
        private InsertComment insertComment;
        private InsertRating insertRating;
        private SearchFood searchFood;
        private CompareFood compareFood;
        private Food food; 
                
        public String getEmailAddress() {
            return emailAddress;
        }
    
        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public String getPassword() {
            return password;
        }
    
        public void setPassword(String password) {
            this.password = password;
        }

        public String getCafeCode() {
            return cafeCode;
        }
    
        public void setCafeCode(String cafeCode) {
            this.cafeCode = cafeCode;
        }

        public String getFoodCode() {
            return foodCode;
        }
    
        public void setFoodCode(String foodCode) {
            this.foodCode = foodCode;
        }
    
        public RegistrationStep getRegistrationStep() {
            return registrationStep;
        }
    
        public void setRegistrationStep(RegistrationStep registrationStep) {
            this.registrationStep = registrationStep;
        }
    
        public InsertStep getInsertStep() {
            return insertStep;
        }
    
        public void setInsertStep(InsertStep insertStep) {
            this.insertStep = insertStep;
        }
    
        public InsertFoodStep getInsertFoodStep() {
            return insertFoodStep;
        }
    
        public void setInsertFoodStep(InsertFoodStep insertFoodStep) {
            this.insertFoodStep = insertFoodStep;
        }

        public UpdateStatus getUpdateStatus() {
            return updateStatus;
        }
    
        public void setUpdateStatus(UpdateStatus updateStatus) {
            this.updateStatus = updateStatus;
        }

        public DeleteFood getDeleteFood() {
            return deleteFood;
        }
    
        public void setDeleteFood(DeleteFood deleteFood) {
            this.deleteFood = deleteFood;
        }

        public Approval getApproval() {
            return approval;
        }
    
        public void setApproval(Approval approval) {
            this.approval = approval;
        }

        public CafeUpdate getCafeUpdate() {
            return cafeUpdate;
        }
    
        public void setCafeUpdate(CafeUpdate cafeUpdate) {
            this.cafeUpdate = cafeUpdate;
        }

        public DeleteCafe getDeleteCafe() {
            return deleteCafe;
        }
    
        public void setDeleteCafe(DeleteCafe deleteCafe) {
            this.deleteCafe = deleteCafe;
        }

        public void setInsertComment(InsertComment insertComment) {
            this.insertComment = insertComment;
        }

        public InsertComment getInsertComment() {
            return insertComment;
        }

        public void setInsertRating(InsertRating insertRating) {
            this.insertRating = insertRating;
        }

        public InsertRating getInsertRating() {
            return insertRating;
        }

        public void setSearchFood(SearchFood searchFood) {
            this.searchFood = searchFood;
        }

        public SearchFood getSearchFood() {
            return searchFood;
        }

        public void setCompareFood(CompareFood compareFood) {
            this.compareFood = compareFood;
        }

        public CompareFood getCompareFood() {
            return compareFood;
        }
    
        public LoginStep getLoginStep() {
            return loginStep;
        }
    
        public void setLoginStep(LoginStep loginStep) {
            this.loginStep = loginStep;
        }
    
        public UserType getUserType() {
            return userType;
        }
    
        public void setUserType(UserType userType) {
            this.userType = userType;
        }
    
        public Food getFood() {
            return food;
        }
    
        public void setFood(Food food) {
            this.food = food;
        }
    }

    private enum CompareFood {
        COMPAREFOOD
    }

    private enum SearchFood {
        SEARCHFOOD
    }

    private enum InsertComment {
        INSERTCOMMENT
    }

    private enum InsertRating {
        INSERTRATINGCODE,
        INSERTRATING
    }

    private enum DeleteCafe {
        DELETECAFE
    }

    private enum CafeUpdate {
        CAFEUPDATE
    }

    private enum Approval {
        APPROVAL
    }

    private enum UpdateStatus {
        UPDATE_STATUS
    }

    private enum DeleteFood {
        DELETE_FOOD
    }

    private enum RegistrationStep {
        REGISTER,
        EMAIL,
        NAME,
        PASSWORD,
    }

    private enum InsertStep {
        INSERTFORM_CAFE
    }

    private enum InsertFoodStep {
        INSERTFOOD_CAFE,
        INSERTFOOD_IMG
    }

    private enum LoginStep {
        EMAIL,
        PASSWORD
    }

    private enum UserType {
        CAFE_ADMIN,
        SYSTEM_ADMIN,
        USER
    }

    public String getBotUsername() {
        return "A222_zinc_bot";
    }

    public String getBotToken() {
        return "6175582859:AAHxqC6OKRKPm7qOfroq0D1WxHGT9jlrALc";
    }
}