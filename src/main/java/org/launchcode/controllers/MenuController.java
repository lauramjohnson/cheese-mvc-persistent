package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by laura on 4/12/2017.
 */
@Controller
@RequestMapping(value = "menu")
public class MenuController {
    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";

    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(@ModelAttribute Menu menu,
                                     Model model){

        model.addAttribute("title", "Create a menu!");
        model.addAttribute(new Menu());


        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model,
                                     @ModelAttribute @Valid Menu menu,
                                     Errors errors) {


        if (errors.hasErrors()) {
            model.addAttribute("title", "Create a menu!");
            return "menu/add";
        }

        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String displayViewMenu(Model model,
                                  @PathVariable int menuId){
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", menu.getName());
        model.addAttribute("cheeses", menu.getCheeses());
        model.addAttribute("menuId", menu.getId());

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String displayAddItem(Model model,
                                 @PathVariable int menuId){

        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm form = new AddMenuItemForm(cheeseDao.findAll(), menu);

        model.addAttribute("title", "Add item to menu:  " + menu.getName());
        model.addAttribute("form", form);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(Model model,
                                 @ModelAttribute @Valid AddMenuItemForm form,
                                 Errors errors){

        if (errors.hasErrors()){
            model.addAttribute("form", form);
            return "menu/add-item/" + form.getMenuId();
        }
        Cheese theCheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(form.getMenuId());
        theMenu.addItem(theCheese);
        menuDao.save(theMenu);

        return "redirect:/menu/view/" + theMenu.getId();
    }
}
