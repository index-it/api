window.swaggerSpec={
  "openapi" : "3.0.3",
  "info" : {
    "title" : "Index - OpenAPI 3.0",
    "description" : "This is the REST api for [Index](https://index-it.app)",
    "termsOfService" : "https://index-it.app/terms/",
    "contact" : {
      "email" : "support@index-it.app"
    },
    "version" : "1.0.0"
  },
  "externalDocs" : {
    "description" : "Learn more about Index",
    "url" : "https://docs.index-it.app/api"
  },
  "servers" : [ {
    "url" : "https://api.index-it.app"
  }, {
    "url" : "https://api-beta.index-it.app"
  } ],
  "paths" : {
    "/welcome-action" : {
      "get" : {
        "tags" : [ "auth" ],
        "summary" : "Retrieve the required first step for a user auth flow",
        "description" : "This is used to initialize a user auth flow.",
        "operationId" : "get-welcome-action",
        "parameters" : [ {
          "name" : "email",
          "in" : "query",
          "description" : "The email of the user",
          "required" : true,
          "schema" : {
            "type" : "string",
            "format" : "email"
          }
        } ],
        "security" : [ ],
        "responses" : {
          "200" : {
            "description" : "Welcome action resolved",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/WelcomeAction"
                }
              }
            }
          }
        }
      }
    },
    "/register" : {
      "post" : {
        "tags" : [ "auth" ],
        "summary" : "Register a user",
        "description" : "Register a user with an email and password",
        "operationId" : "register",
        "requestBody" : {
          "required" : true,
          "content" : {
            "application/json" : {
              "schema" : {
                "$ref" : "#/components/schemas/Credentials"
              }
            }
          }
        },
        "security" : [ ],
        "responses" : {
          "200" : {
            "description" : "Registered and sent verification email"
          },
          "201" : {
            "description" : "Registered but did not send verification email for some internal issues. It should be requested with the related endpoint"
          },
          "400" : {
            "description" : "Email or password format not satisfied (see response message)"
          },
          "403" : {
            "description" : "The email cannot be used to register"
          }
        }
      }
    },
    "/send-verification-email" : {
      "post" : {
        "tags" : [ "email-verification" ],
        "summary" : "Sends an email to register an account",
        "description" : "Sends an email to verify a register account",
        "operationId" : "send-verification-email",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/EmailVerificationAuth"
        },
        "security" : [ ],
        "responses" : {
          "200" : {
            "description" : "email already verified"
          },
          "201" : {
            "description" : "verification email sent"
          },
          "403" : {
            "description" : "Not authenticated"
          }
        }
      }
    },
    "/verify-email" : {
      "get" : {
        "tags" : [ "email-verification" ],
        "summary" : "Verifies an email via the code sent to the email",
        "operationId" : "verify-email",
        "parameters" : [ {
          "name" : "token",
          "in" : "query",
          "description" : "The verification code",
          "required" : true,
          "schema" : {
            "type" : "string"
          }
        } ],
        "security" : [ ],
        "responses" : {
          "302" : {
            "description" : "Redirects to either a success page or error page"
          }
        }
      }
    },
    "/is-email-verified" : {
      "post" : {
        "tags" : [ "email-verification" ],
        "summary" : "Check if an email is verified",
        "operationId" : "is-email-verified",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/EmailVerificationAuth"
        },
        "security" : [ ],
        "responses" : {
          "200" : {
            "description" : "Email is verified"
          },
          "404" : {
            "description" : "Email is not verified"
          },
          "403" : {
            "description" : "Not authenticated"
          }
        }
      }
    },
    "/login" : {
      "post" : {
        "tags" : [ "auth" ],
        "summary" : "Login and get a session",
        "description" : "Login and create a session for your account",
        "operationId" : "login",
        "requestBody" : {
          "required" : true,
          "content" : {
            "application/json" : {
              "schema" : {
                "$ref" : "#/components/schemas/Credentials"
              }
            }
          }
        },
        "security" : [ ],
        "responses" : {
          "200" : {
            "description" : "Logged in, session cookie created",
            "headers" : {
              "Set-Cookie" : {
                "schema" : {
                  "type" : "string"
                }
              }
            }
          },
          "403" : {
            "description" : "Unauthorized"
          },
          "405" : {
            "description" : "The email of the user is not verified"
          }
        }
      }
    },
    "/logout" : {
      "get" : {
        "tags" : [ "auth" ],
        "summary" : "Log out and deletes the auth session",
        "operationId" : "logout",
        "responses" : {
          "200" : {
            "description" : "Logged out"
          }
        }
      }
    },
    "/password-forgotten" : {
      "get" : {
        "tags" : [ "password-operations" ],
        "operationId" : "password-forgotten",
        "parameters" : [ {
          "name" : "email",
          "in" : "query",
          "description" : "The email of the user",
          "required" : true,
          "schema" : {
            "type" : "string",
            "format" : "email"
          }
        } ],
        "responses" : {
          "200" : {
            "description" : "Sent an email with instructions on how to reset the password"
          },
          "404" : {
            "description" : "User not found"
          },
          "429" : {
            "description" : "Too many password forgotten requests"
          }
        }
      }
    },
    "/reset-password" : {
      "post" : {
        "tags" : [ "password-operations" ],
        "operationId" : "reset-password",
        "parameters" : [ {
          "name" : "token",
          "in" : "query",
          "description" : "The token to authenticate the password reset request",
          "required" : true,
          "schema" : {
            "type" : "string"
          }
        } ],
        "requestBody" : {
          "required" : true,
          "content" : {
            "application/json" : {
              "schema" : {
                "type" : "object",
                "properties" : {
                  "password" : {
                    "type" : "string",
                    "format" : "password",
                    "description" : "The new password"
                  }
                }
              }
            }
          }
        },
        "responses" : {
          "200" : {
            "description" : "Password resetted, user sessions deleted and operation notification email sent"
          },
          "400" : {
            "description" : "Password security policies not matched"
          },
          "404" : {
            "description" : "Token not valid"
          }
        }
      }
    },
    "/me" : {
      "get" : {
        "tags" : [ "user" ],
        "operationId" : "get-user",
        "responses" : {
          "200" : {
            "description" : "get the informations of the logged in account",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/User"
                }
              }
            }
          }
        }
      },
      "delete" : {
        "tags" : [ "user" ],
        "operationId" : "delete-user",
        "responses" : {
          "200" : {
            "description" : "User data deleted completely"
          }
        }
      }
    },
    "/lists" : {
      "get" : {
        "tags" : [ "lists" ],
        "operationId" : "get-lists",
        "responses" : {
          "200" : {
            "description" : "Array with all the lists of the logged in user",
            "content" : {
              "application/json" : {
                "schema" : {
                  "type" : "array",
                  "items" : {
                    "$ref" : "#/components/schemas/List"
                  }
                }
              }
            }
          }
        }
      },
      "post" : {
        "tags" : [ "lists" ],
        "operationId" : "create-list",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/ListCreateBody"
        },
        "responses" : {
          "200" : {
            "description" : "Creates a new List",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/List"
                }
              }
            }
          },
          "400" : {
            "description" : "Invalid request body (see response message)"
          }
        }
      }
    },
    "/lists/template" : {
      "get" : {
        "tags" : [ "lists" ],
        "operationId" : "get-list-template",
        "responses" : {
          "200" : {
            "description" : "A list template that contains a name and a color",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/ListTemplate"
                }
              }
            }
          }
        }
      }
    },
    "/lists/template/colors" : {
      "get" : {
        "tags" : [ "lists" ],
        "operationId" : "get-list-template-colors",
        "responses" : {
          "200" : {
            "description" : "A list of default colors usable for lists",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/ListTemplateColors"
                }
              }
            }
          }
        }
      }
    },
    "/lists/{listId}" : {
      "parameters" : [ {
        "name" : "listId",
        "in" : "path",
        "description" : "The id of the List",
        "required" : true,
        "schema" : {
          "type" : "string"
        }
      } ],
      "get" : {
        "tags" : [ "lists" ],
        "operationId" : "get-list",
        "responses" : {
          "200" : {
            "description" : "A single List",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/List"
                }
              }
            }
          },
          "404" : {
            "description" : "List with id listId not found"
          }
        }
      },
      "put" : {
        "tags" : [ "lists" ],
        "operationId" : "update-list",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/ListUpdateBody"
        },
        "responses" : {
          "200" : {
            "description" : "Updated list",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/List"
                }
              }
            }
          },
          "400" : {
            "description" : "Invalid request body (see response message)"
          },
          "404" : {
            "description" : "List with id listId not found"
          }
        }
      },
      "delete" : {
        "tags" : [ "lists" ],
        "operationId" : "delete-list",
        "responses" : {
          "200" : {
            "description" : "List deleted and all items of the list deleted"
          }
        }
      }
    },
    "/lists/{listId}/categories" : {
      "parameters" : [ {
        "name" : "listId",
        "in" : "path",
        "description" : "The id of the List",
        "required" : true,
        "schema" : {
          "type" : "string"
        }
      } ],
      "get" : {
        "tags" : [ "list-categories" ],
        "operationId" : "get-list-categories",
        "responses" : {
          "200" : {
            "description" : "Array with all the categories of a List",
            "content" : {
              "application/json" : {
                "schema" : {
                  "type" : "array",
                  "items" : {
                    "$ref" : "#/components/schemas/Category"
                  }
                }
              }
            }
          },
          "404" : {
            "description" : "List not found"
          }
        }
      },
      "post" : {
        "tags" : [ "list-categories" ],
        "operationId" : "create-list-category",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/CategoryCreateBody"
        },
        "responses" : {
          "200" : {
            "description" : "Creates a new Category for a List",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/Category"
                }
              }
            }
          },
          "400" : {
            "description" : "Invalid request body (see response message)"
          },
          "404" : {
            "description" : "List not found"
          }
        }
      }
    },
    "/lists/{listId}/categories/{categoryId}" : {
      "parameters" : [ {
        "name" : "listId",
        "in" : "path",
        "description" : "The id of the List",
        "required" : true,
        "schema" : {
          "type" : "string"
        }
      }, {
        "name" : "categoryId",
        "in" : "path",
        "description" : "The id of the Category",
        "required" : true,
        "schema" : {
          "type" : "string"
        }
      } ],
      "get" : {
        "tags" : [ "list-categories" ],
        "operationId" : "get-list-category",
        "responses" : {
          "200" : {
            "description" : "The Category of the List",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/Category"
                }
              }
            }
          },
          "404" : {
            "description" : "List not found"
          }
        }
      },
      "put" : {
        "tags" : [ "list-categories" ],
        "operationId" : "update-list-category",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/CategoryUpdateBody"
        },
        "responses" : {
          "200" : {
            "description" : "Category updated",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/Category"
                }
              }
            }
          },
          "400" : {
            "description" : "Invalid request body (see response message)"
          },
          "404" : {
            "description" : "List not found"
          }
        }
      },
      "delete" : {
        "tags" : [ "list-categories" ],
        "operationId" : "delete-list-category",
        "responses" : {
          "200" : {
            "description" : "Category deleted and items of that category deleted"
          }
        }
      }
    },
    "/lists/{listId}/items" : {
      "parameters" : [ {
        "name" : "listId",
        "in" : "path",
        "description" : "The id of the List",
        "required" : true,
        "schema" : {
          "type" : "string"
        }
      } ],
      "get" : {
        "tags" : [ "list-items" ],
        "operationId" : "get-list-items",
        "responses" : {
          "200" : {
            "description" : "All the Items of a List",
            "content" : {
              "application/json" : {
                "schema" : {
                  "type" : "array",
                  "items" : {
                    "$ref" : "#/components/schemas/Item"
                  }
                }
              }
            }
          }
        }
      },
      "post" : {
        "tags" : [ "list-items" ],
        "operationId" : "create-list-item",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/ItemCreateBody"
        },
        "responses" : {
          "200" : {
            "description" : "New Item created",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/Item"
                }
              }
            }
          },
          "400" : {
            "description" : "Invalid request body (see response message)"
          }
        }
      }
    },
    "/lists/{listId}/items/{itemId}" : {
      "parameters" : [ {
        "name" : "listId",
        "in" : "path",
        "description" : "The id of the Iist",
        "required" : true,
        "schema" : {
          "type" : "string"
        }
      }, {
        "name" : "itemId",
        "in" : "path",
        "description" : "The id of the Item",
        "required" : true,
        "schema" : {
          "type" : "string"
        }
      } ],
      "get" : {
        "tags" : [ "list-items" ],
        "operationId" : "get-list-item",
        "responses" : {
          "200" : {
            "description" : "Get an Item of a List",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/Item"
                }
              }
            }
          },
          "404" : {
            "description" : "List not found"
          }
        }
      },
      "put" : {
        "tags" : [ "list-items" ],
        "operationId" : "update-list-item",
        "requestBody" : {
          "$ref" : "#/components/requestBodies/ItemUpdateBody"
        },
        "responses" : {
          "200" : {
            "description" : "Item updated",
            "content" : {
              "application/json" : {
                "schema" : {
                  "$ref" : "#/components/schemas/Item"
                }
              }
            }
          },
          "400" : {
            "description" : "Invalid request body (see response message)"
          },
          "404" : {
            "description" : "List not found"
          }
        }
      },
      "delete" : {
        "tags" : [ "list-items" ],
        "operationId" : "delete-list-item",
        "responses" : {
          "200" : {
            "description" : "Item deleted"
          }
        }
      }
    },
    "/ws" : {
      "get" : {
        "tags" : [ "websocket" ],
        "operationId" : "websocket-connect",
        "responses" : {
          "201" : {
            "description" : "Connected to websocket"
          }
        }
      }
    }
  },
  "components" : {
    "schemas" : {
      "WelcomeAction" : {
        "type" : "object",
        "properties" : {
          "action" : {
            "type" : "string",
            "enum" : [ "register", "login" ]
          }
        }
      },
      "Credentials" : {
        "type" : "object",
        "properties" : {
          "email" : {
            "type" : "string",
            "format" : "email"
          },
          "password" : {
            "type" : "string",
            "format" : "password"
          }
        }
      },
      "User" : {
        "type" : "object",
        "properties" : {
          "email" : {
            "type" : "string",
            "format" : "email"
          },
          "creationTimestamp" : {
            "type" : "number",
            "format" : "timestamp"
          },
          "creationSource" : {
            "type" : "string",
            "enum" : [ "google", "apple", "facebook", "none" ]
          }
        }
      },
      "List" : {
        "type" : "object",
        "properties" : {
          "_id" : {
            "type" : "string"
          },
          "userId" : {
            "type" : "string"
          },
          "name" : {
            "type" : "string"
          },
          "categories" : {
            "type" : "array",
            "items" : {
              "$ref" : "#/components/schemas/Category"
            }
          },
          "icon" : {
            "type" : "string"
          },
          "color" : {
            "type" : "string",
            "format" : "#AARRGGBB"
          }
        }
      },
      "ListTemplate" : {
        "type" : "object",
        "properties" : {
          "name" : {
            "type" : "string"
          },
          "color" : {
            "type" : "string"
          }
        }
      },
      "ListTemplateColors" : {
        "type" : "object",
        "properties" : {
          "_id" : {
            "type" : "string"
          },
          "description" : {
            "type" : "string"
          },
          "colors" : {
            "type" : "array",
            "items" : {
              "type" : "string",
              "format" : "#AARRGGBB"
            }
          }
        }
      },
      "Category" : {
        "type" : "object",
        "properties" : {
          "_id" : {
            "type" : "string"
          },
          "name" : {
            "type" : "string"
          },
          "color" : {
            "type" : "string",
            "format" : "#AARRGGBB"
          }
        }
      },
      "Item" : {
        "type" : "object",
        "properties" : {
          "_id" : {
            "type" : "string"
          },
          "userId" : {
            "type" : "string"
          },
          "listId" : {
            "type" : "string"
          },
          "categoryId" : {
            "type" : "string"
          },
          "name" : {
            "type" : "string"
          }
        }
      }
    },
    "securitySchemes" : {
      "sessionCookie" : {
        "type" : "apiKey",
        "in" : "cookie",
        "name" : "user_session_id"
      }
    },
    "requestBodies" : {
      "EmailVerificationAuth" : {
        "required" : true,
        "description" : "authentication needed for the routes related to email verification\n",
        "content" : {
          "application/x-www-form-urlencoded" : {
            "schema" : {
              "type" : "object",
              "properties" : {
                "email" : {
                  "type" : "string",
                  "format" : "email"
                },
                "password" : {
                  "type" : "string",
                  "format" : "password"
                }
              },
              "required" : [ "email", "password" ]
            }
          }
        }
      },
      "ListCreateBody" : {
        "required" : true,
        "description" : "Creates a new list with the passed properties\n",
        "content" : {
          "application/json" : {
            "schema" : {
              "type" : "object",
              "properties" : {
                "name" : {
                  "type" : "string"
                },
                "icon" : {
                  "type" : "string"
                },
                "color" : {
                  "type" : "string",
                  "format" : "#AARRGGBB"
                }
              },
              "required" : [ "name", "icon", "color" ]
            }
          }
        }
      },
      "ListUpdateBody" : {
        "required" : true,
        "description" : "Non null values of this object will update the respective property of a list (null values are ignored)\n",
        "content" : {
          "application/json" : {
            "schema" : {
              "type" : "object",
              "properties" : {
                "name" : {
                  "type" : "string"
                },
                "icon" : {
                  "type" : "string"
                },
                "color" : {
                  "type" : "string",
                  "format" : "#AARRGGBB"
                }
              }
            }
          }
        }
      },
      "ItemCreateBody" : {
        "required" : true,
        "description" : "Creates a new item with the passed properties\n",
        "content" : {
          "application/json" : {
            "schema" : {
              "type" : "object",
              "properties" : {
                "name" : {
                  "type" : "string"
                },
                "categoryId" : {
                  "type" : "string"
                }
              },
              "required" : [ "name", "categoryId" ]
            }
          }
        }
      },
      "ItemUpdateBody" : {
        "required" : true,
        "description" : "Non null values of this object will update the respective property of an item (null values are ignored)\n",
        "content" : {
          "application/json" : {
            "schema" : {
              "type" : "object",
              "properties" : {
                "name" : {
                  "type" : "string"
                },
                "categoryId" : {
                  "type" : "string"
                }
              }
            }
          }
        }
      },
      "CategoryCreateBody" : {
        "required" : true,
        "description" : "Creates a new category with the passed properties\n",
        "content" : {
          "application/json" : {
            "schema" : {
              "type" : "object",
              "properties" : {
                "name" : {
                  "type" : "string"
                },
                "color" : {
                  "type" : "string",
                  "format" : "#AARRGGBB"
                }
              },
              "required" : [ "name", "color" ]
            }
          }
        }
      },
      "CategoryUpdateBody" : {
        "required" : true,
        "description" : "Non null values of this object will update the respective property of a category (null values are ignored)\n",
        "content" : {
          "application/json" : {
            "schema" : {
              "type" : "object",
              "properties" : {
                "name" : {
                  "type" : "string"
                },
                "color" : {
                  "type" : "string",
                  "format" : "#AARRGGBB"
                }
              }
            }
          }
        }
      }
    }
  },
  "security" : [ {
    "sessionCookie" : [ ]
  } ]
}